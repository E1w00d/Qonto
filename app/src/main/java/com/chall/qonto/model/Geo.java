package com.chall.qonto.model;

import com.chall.qonto.utils.Utils;
import com.google.gson.annotations.SerializedName;

public final class Geo {
	@SerializedName("lat")
	private String mLat;

	@SerializedName("lng")
	private String mLng;

	public final double getLatitude() {
		return Utils.parseDouble(this.mLat, .0D);
	}

	public final double getLongitude() {
		return Utils.parseDouble(this.mLng, .0D);
	}
}
