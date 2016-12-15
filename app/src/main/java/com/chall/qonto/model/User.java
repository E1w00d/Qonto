package com.chall.qonto.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public final class User {
	@SerializedName("id")
	private long mUserId;

	@SerializedName("name")
	private String mLastName;

	@SerializedName("username")
	private String mFirstName;

	@SerializedName("email")
	private String mEmail;

	@SerializedName("address")
	private Address mAddress;

	@SerializedName("phone")
	private String mPhone;

	@SerializedName("website")
	private String mWebsite;

	@SerializedName("company")
	private Company mCompany;

	public final long getUserId() {
		return this.mUserId;
	}

	public final String getLastName() {
		return this.mLastName;
	}

	public final String getFirstName() {
		return this.mFirstName;
	}

	public final String getFullName() {
		return TextUtils.isEmpty(this.mLastName)
					? this.mFirstName
					: TextUtils.isEmpty(this.mFirstName)
						? this.mLastName
						: String.format("%s %s", this.mFirstName, this.mLastName);
	}

	public final String getEmail() {
		return this.mEmail;
	}

	public final boolean hasAddress() {
		return null != this.mAddress;

	}

	public final Address getAddress() {
		return this.mAddress;
	}

	public final String getPhone() {
		return this.mPhone;
	}

	public final String getWebsite() {
		return this.mWebsite;
	}

	public final boolean hasCompany() {
		return null != this.mCompany;

	}

	public final Company getCompany() {
		return this.mCompany;
	}
}
