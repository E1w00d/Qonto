package com.chall.qonto.model;

import com.google.gson.annotations.SerializedName;

public final class Photo {
	@SerializedName("albumId")
	private long mAlbumId;

	@SerializedName("id")
	private long mPhotoId;

	@SerializedName("title")
	private String mTitle;

	@SerializedName("url")
	private String mUrl;

	@SerializedName("thumbnailUrl")
	private String mThumbnail;

	public final long getAlbumId() {
		return this.mAlbumId;
	}

	public final long getPhotoId() {
		return this.mPhotoId;
	}

	public final String getTitle() {
		return this.mTitle;
	}

	public final String getPhotoUrl() {
		return this.mUrl;
	}

	public final String getThumbnailUrl() {
		return mThumbnail;
	}
}
