package com.chall.qonto.model;

import com.google.gson.annotations.SerializedName;

public final class Album {
	@SerializedName("userId")
	private long mUserId;

	@SerializedName("id")
	private long mAlbumId;

	@SerializedName("title")
	private String mTitle;

	public final long getUserId() {
		return this.mUserId;
	}

	public final long getAlbumId() {
		return this.mAlbumId;
	}

	public final String getTitle() {
		return this.mTitle;
	}
}
