package com.chall.qonto.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.chall.qonto.BuildConfig;
import com.chall.qonto.utils.ArrayUtils;
import com.chall.qonto.utils.Preconditions;
import com.chall.qonto.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public final class DatabaseProvider extends ContentProvider {
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private static final String AUTHORITY = String.format("%s.provider", BuildConfig.APPLICATION_ID);

	private static final Uri CONTENT_URI = Utils.parseUri(String.format("content://%s", DatabaseProvider.AUTHORITY));

	public static final Uri USER_URI = Uri.withAppendedPath(DatabaseProvider.CONTENT_URI, "users");
	public static final Uri ALBUM_URI = Uri.withAppendedPath(DatabaseProvider.CONTENT_URI, "albums");
	public static final Uri PHOTO_URI = Uri.withAppendedPath(DatabaseProvider.CONTENT_URI, "photos");

	private static final Map<String, String> COUNT_PROJECTION_MAP = new HashMap<>(2);
	private static final Map<String, String> USER_PROJECTION_MAP = new HashMap<>(8);
	private static final Map<String, String> ALBUM_PROJECTION_MAP = new HashMap<>(6);
	private static final Map<String, String> PHOTO_PROJECTION_MAP = new HashMap<>(8);

	static {
		final UriMatcher matcher = DatabaseProvider.sURIMatcher;

		matcher.addURI(DatabaseProvider.AUTHORITY, "users", 10);
		matcher.addURI(DatabaseProvider.AUTHORITY, "users/id/#", 11);

		matcher.addURI(DatabaseProvider.AUTHORITY, "albums", 20);
		matcher.addURI(DatabaseProvider.AUTHORITY, "albums/id/#", 21);

		matcher.addURI(DatabaseProvider.AUTHORITY, "photos", 30);
		matcher.addURI(DatabaseProvider.AUTHORITY, "photos/id/#", 31);

		DatabaseProvider.COUNT_PROJECTION_MAP.put(BaseColumns._COUNT, "COUNT(*)");

		DatabaseProvider.USER_PROJECTION_MAP.put(BaseColumns._ID, "_id");
		DatabaseProvider.USER_PROJECTION_MAP.put("user_id", "user_id");
		DatabaseProvider.USER_PROJECTION_MAP.put("full_name", "full_name");
		DatabaseProvider.USER_PROJECTION_MAP.put("phone", "phone");
		DatabaseProvider.USER_PROJECTION_MAP.put("email", "email");
		DatabaseProvider.USER_PROJECTION_MAP.put("website", "website");


		DatabaseProvider.ALBUM_PROJECTION_MAP.put(BaseColumns._ID, "_id");
		DatabaseProvider.ALBUM_PROJECTION_MAP.put("user_id", "user_id");
		DatabaseProvider.ALBUM_PROJECTION_MAP.put("album_id", "album_id");
		DatabaseProvider.ALBUM_PROJECTION_MAP.put("title", "title");

		DatabaseProvider.PHOTO_PROJECTION_MAP.put(BaseColumns._ID, "_id");
		DatabaseProvider.PHOTO_PROJECTION_MAP.put("album_id", "album_id");
		DatabaseProvider.PHOTO_PROJECTION_MAP.put("photo_id", "photo_id");
		DatabaseProvider.PHOTO_PROJECTION_MAP.put("title", "title");
		DatabaseProvider.PHOTO_PROJECTION_MAP.put("url", "url");
		DatabaseProvider.PHOTO_PROJECTION_MAP.put("thumbnail", "thumbnail");

	}

	public static Uri buildUserUri(final long userId) {
		final Uri.Builder builder = DatabaseProvider.USER_URI.buildUpon();

		builder.appendPath("id").appendPath(String.valueOf(userId));

		return builder.build();
	}

	public static Uri buildAlbumUri(final long userId) {
		final Uri.Builder builder = DatabaseProvider.ALBUM_URI.buildUpon();

		builder.appendPath("id").appendPath(String.valueOf(userId));

		return builder.build();
	}

	public static Uri buildPhotoUri(final long albumId) {
		final Uri.Builder builder = DatabaseProvider.PHOTO_URI.buildUpon();

		builder.appendPath("id").appendPath(String.valueOf(albumId));

		return builder.build();
	}

	@Override
	public final boolean onCreate() {
		return true;
	}

	@Override
	public final Cursor query(@NonNull final Uri uri, final String[] projection,
							  final String selection, String[] selectionArgs, String sortOrder) {
		final DatabaseHelper db;

		final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		switch (DatabaseProvider.findMatch(uri, "query")) {
			case 11:
				builder.appendWhere("user_id=?");

				selectionArgs = ArrayUtils.prependStrings(selectionArgs, uri.getPathSegments().get(2));
			case 10:
				builder.setTables("users");

				builder.setProjectionMap(DatabaseProvider.USER_PROJECTION_MAP);

				db = DatabaseFactory.getApplicationDatabaseHelper(this.getContext());
				break;
			case 21:
				builder.appendWhere("user_id=?");

				selectionArgs = ArrayUtils.prependStrings(selectionArgs, uri.getPathSegments().get(2));
			case 20:
				builder.setTables("albums");

				builder.setProjectionMap(DatabaseProvider.ALBUM_PROJECTION_MAP);

				db = DatabaseFactory.getApplicationDatabaseHelper(this.getContext());
				break;
			case 31:
				builder.appendWhere("album_id=?");

				selectionArgs = ArrayUtils.prependStrings(selectionArgs, uri.getPathSegments().get(2));
			case 30:
				builder.setTables("photos");

				builder.setProjectionMap(DatabaseProvider.PHOTO_PROJECTION_MAP);

				db = DatabaseFactory.getApplicationDatabaseHelper(this.getContext());
				break;
			default :
				throw new IllegalArgumentException(String.format("Unknown URI: %s", uri));
		}

		if (null != projection && 1 == projection.length && BaseColumns._COUNT.equals(projection[0])) {
			builder.setProjectionMap(DatabaseProvider.COUNT_PROJECTION_MAP);
		}

		Cursor cursor = null;

		try {
			cursor = builder.query(db.getReadableDatabase(),
								   projection, selection, selectionArgs, null, null, sortOrder, null);

			if (null != cursor && !this.isTemporary()) {
				//noinspection ConstantConditions
				cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
			}
		}
		catch (final SQLException ex) {
			Timber.tag("Storage")
				  .wtf(ex, "Query %s has failed", builder.buildQuery(projection, selection, null, null, sortOrder, null));
		}

		return cursor;
	}

	@Override
	public final String getType(@NonNull final Uri uri) {
		switch (DatabaseProvider.findMatch(uri, "getType")) {
			case 10:
				return ContentResolver.CURSOR_DIR_BASE_TYPE  + "/vnd.qonto.user";
			case 11:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qonto.user";
			case 20:
				return ContentResolver.CURSOR_DIR_BASE_TYPE  + "/vnd.qonto.album";
			case 21:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qonto.album";
			case 30:
				return ContentResolver.CURSOR_DIR_BASE_TYPE  + "/vnd.qonto.photo";
			case 31:
				return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.qonto.photo";
			default :
				throw new IllegalArgumentException(String.format("Unknown URI: %s", uri));
		}
	}

	@Override
	public final Uri insert(@NonNull final Uri uri, final ContentValues values) {
		throw new UnsupportedOperationException("Cannot insert into DbProvider");
	}

	@Override
	public final int delete(@NonNull final Uri uri, final String selection, final String[] selectionArgs) {
		throw new UnsupportedOperationException("Cannot delete from DbProvider");
	}

	@Override
	public final int update(@NonNull final Uri uri, final ContentValues values,
							final String selection, final String[] selectionArgs) {
		throw new UnsupportedOperationException("Cannot update DbProvider");
	}

	private static int findMatch(final Uri uri, final String methodName) {
		final int match;

		Preconditions.checkArgument(0 < (match = DatabaseProvider.sURIMatcher.match(uri)), "Unknown uri: %s", uri);

		Timber.tag("Storage").d("%s: uri=%s, match is %d", methodName, uri, match);

		return match;
	}
}
