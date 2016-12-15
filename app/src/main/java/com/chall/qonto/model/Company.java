package com.chall.qonto.model;

import com.google.gson.annotations.SerializedName;

public final class Company {
	@SerializedName("bs")
	private String mBs;

	@SerializedName("name")
	private String mName;

	@SerializedName("catchPhrase")
	private String mCatchPhrase;

	public final String getBs() {
		return this.mBs;
	}

	public final String getName() {
		return this.mName;
	}

	public final String getCatchPhrase() {
		return this.mCatchPhrase;
	}
}
