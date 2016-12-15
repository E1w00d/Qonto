package com.chall.qonto.api;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.chall.qonto.BuildConfig;
import com.chall.qonto.R;
import com.chall.qonto.async.AsyncModule;
import com.chall.qonto.async.QualifierAnnotations;
import com.chall.qonto.data.DatabaseFactory;
import com.chall.qonto.data.DatabaseProvider;
import com.chall.qonto.datastore.ApplicationStorage;
import com.chall.qonto.model.Album;
import com.chall.qonto.model.Photo;
import com.chall.qonto.model.User;
import com.chall.qonto.utils.EventBusModule;
import com.chall.qonto.utils.NetworkUtils;
import com.chall.qonto.utils.Preconditions;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public final class RestClient {
	@QualifierAnnotations.Parallel
	private static final Executor THREAD_POOL_EXECUTOR;

	private static RestClient sInstance;

	private final RestAPI mRestService;
	private final WeakReference<Context> mContext;

	static {
		THREAD_POOL_EXECUTOR = AsyncModule.getParallelExecutor();

		((ThreadPoolExecutor)RestClient.THREAD_POOL_EXECUTOR).prestartCoreThread();
		((ThreadPoolExecutor)RestClient.THREAD_POOL_EXECUTOR).allowCoreThreadTimeOut(true);
	}

	public static RestClient getInstance() {
		return Preconditions.checkNotNull(RestClient.sInstance);
	}

	public static void initialize(@NonNull final Context context) {
		if (null == RestClient.sInstance) {
			RestClient.sInstance = new RestClient(context.getApplicationContext());
		}
	}

	private RestClient(@NonNull final Context context) {
		this.mContext = new WeakReference<>(context);

		final OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(80L, TimeUnit.SECONDS)
																	   .writeTimeout(80L, TimeUnit.SECONDS)
																	   .connectTimeout(30L, TimeUnit.SECONDS);

		this.mRestService = new Retrofit.Builder().callFactory(builder.build())
												  .validateEagerly(BuildConfig.DEBUG)
												  .baseUrl(context.getString(R.string.base_url))
												  .callbackExecutor(RestClient.THREAD_POOL_EXECUTOR)
												  .addConverterFactory(GsonConverterFactory.create())
												  .build()
												  .create(RestAPI.class);
	}

	//region Handle HTTP requests
	public final void handleRequest(@NonNull final Bundle args) {
		if (!NetworkUtils.hasNetworkConnection(this.mContext.get())) {
			//noinspection ResourceType
			final ResponseEvent event = new ResponseEvent(args.getInt("req_id"));

			event.mConnectivity = false;
			event.mError = this.mContext.get().getString(R.string.network_error);

			EventBusModule.post(event);
		}
		else {
			@RequestType
			final int opCode;

			//noinspection ResourceType
			switch (opCode = args.getInt("req_id")) {
				case RequestType.USERS:
					this.getUsers();
					break;
				case RequestType.ALBUMS:
					this.getAlbums(args);
					break;
				case RequestType.PHOTOS:
					this.getPhotos(args);
					break;
				default:
					throw new IllegalArgumentException(String.format("Unhandled operation: %d", opCode));
			}
		}
	}
	//endregion

	//region Handle HTTP responses
	private void handleResponse(@RequestType final int opCode, final Object object) {
		final ResponseEvent event;

		if (object instanceof Throwable) {
			Timber.tag("HTTP").wtf((Throwable)object, "Request has failed");

			event = new ResponseEvent(opCode);

			event.mError = this.mContext.get().getString(R.string.error_message);

			event.mConnectivity = NetworkUtils.hasNetworkConnection(this.mContext.get());
		}
		else if (object instanceof retrofit2.Response) {
			event = new ResponseEvent(opCode);

			final Object result;
			final retrofit2.Response response = (retrofit2.Response)object;

			switch (opCode) {
				case HttpURLConnection.HTTP_UNAVAILABLE:
				case HttpURLConnection.HTTP_BAD_GATEWAY:
				case HttpURLConnection.HTTP_INTERNAL_ERROR:
				case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
				case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
					event.mError = this.mContext.get().getString(R.string.server_error);
					break;
				case RequestType.USERS:
					if (response.isSuccessful() && (result = response.body()) instanceof List<?>) {
						//noinspection unchecked
						((ApplicationStorage)DatabaseFactory.getApplicationStorage(this.mContext.get())).updateUsersInTransaction((List<User>)result);

						this.mContext.get().getContentResolver().notifyChange(DatabaseProvider.USER_URI, null);
					}
					break;
				case RequestType.ALBUMS:
					if (response.isSuccessful() && (result = response.body()) instanceof List<?>) {
						//noinspection unchecked
						((ApplicationStorage)DatabaseFactory.getApplicationStorage(this.mContext.get())).updateAlbumsTransaction((List<Album>)result);

						this.mContext.get().getContentResolver().notifyChange(DatabaseProvider.ALBUM_URI, null);
					}
					break;
				case RequestType.PHOTOS:
					if (response.isSuccessful() && (result = response.body()) instanceof List<?>) {
						//noinspection unchecked
						((ApplicationStorage)DatabaseFactory.getApplicationStorage(this.mContext.get())).updatePhotosTransaction((List<Photo>)result);

						this.mContext.get().getContentResolver().notifyChange(DatabaseProvider.PHOTO_URI, null);
					}
					break;
				default:
					break;
			}
		}
		else {
			throw new IllegalArgumentException("Result must be a Response or a Throwable.");
		}

		EventBusModule.post(event);
	}
	//endregion

	//region HTTP calls
	private void getUsers() {
		this.mRestService.getUsers()
						 .enqueue(new Callback<List<User>>() {
							 @Override
							 final void success(@NonNull final Response<List<User>> response) {
								 RestClient.this.handleResponse(RequestType.USERS, response);
							 }

							 @Override
							 final void failure(@NonNull final Throwable t) {
								 RestClient.this.handleResponse(RequestType.USERS, t);
							 }
						 });
	}

	private void getAlbums(final Bundle args) {
		this.mRestService.getAlbums(args.getLong("user_id"))
						 .enqueue(new Callback<List<Album>>() {
							 @Override
							 final void success(@NonNull final Response<List<Album>> response) {
								 RestClient.this.handleResponse(RequestType.ALBUMS, response);
							 }

							 @Override
							 final void failure(@NonNull final Throwable t) {
								 RestClient.this.handleResponse(RequestType.ALBUMS, t);
							 }
						 });
	}

	private void getPhotos(final Bundle args) {
		this.mRestService.getPhotos(args.getLong("album_id"))
						 .enqueue(new Callback<List<Photo>>() {
							 @Override
							 final void success(@NonNull final Response<List<Photo>> response) {
								 RestClient.this.handleResponse(RequestType.PHOTOS, response);
							 }

							 @Override
							 final void failure(@NonNull final Throwable t) {
								 RestClient.this.handleResponse(RequestType.PHOTOS, t);
							 }
						 });
	}
	//endregion
}
