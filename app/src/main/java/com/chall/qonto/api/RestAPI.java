package com.chall.qonto.api;

import com.chall.qonto.model.Album;
import com.chall.qonto.model.Photo;
import com.chall.qonto.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface RestAPI {
	@GET("users")
	Call<List<User>> getUsers();

	@GET("users/{user_id}/albums")
	Call<List<Album>> getAlbums(@Path("user_id") final long userId);

	@GET("albums/{album_id}/photos")
	Call<List<Photo>> getPhotos(@Path("album_id") final long albumId);
}
