package com.chall.qonto.api;

import android.support.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Response;

abstract class Callback <T> implements retrofit2.Callback<T> {
	abstract void failure(@NonNull final Throwable t);
	abstract void success(@NonNull final Response<T> response);

	@Override
	public final void onResponse(final Call<T> call, final Response<T> response) {
		this.success(response);
	}

	@Override
	public final void onFailure(final Call<T> call, final Throwable t) {
		this.failure(t);
	}
}
