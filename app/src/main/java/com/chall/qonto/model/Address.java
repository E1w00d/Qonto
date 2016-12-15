package com.chall.qonto.model;

import com.google.gson.annotations.SerializedName;

public final class Address {
	@SerializedName("street")
	private String mStreet;

	@SerializedName("suite")
	private String mSuite;

	@SerializedName("city")
	private String mCity;

	@SerializedName("zipcode")
	private String mZipcode;

	@SerializedName("geo")
	private Geo mGeo;

	public final String getStreet() {
		return this.mStreet;
	}

	public final String getSuite() {
		return this.mSuite;
	}

	public final String getCity() {
		return this.mCity;
	}

	public final String getZipCode() {
		return this.mZipcode;
	}

	public final boolean hasGeo() {
		return null != this.mGeo;
	}

	public final Geo getGeo() {
		return this.mGeo;
	}
}
