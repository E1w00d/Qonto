package com.chall.qonto.api;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public final class ResponseEvent {
	String mError;

	boolean mConnectivity = true;

	@RequestType
	private final int mRequestId;

	public ResponseEvent(@RequestType final int reqId) {
		this.mRequestId = reqId;
	}

	public final boolean hasConnectivity() {
		return this.mConnectivity;
	}

	public final boolean hasError() {
		return null != this.mError;
	}

	@RequestType
	public final int getRequestId() {
		return this.mRequestId;
	}

	@NonNull
	public final String getErrorMessage() {
		return !TextUtils.isEmpty(this.mError)
					? this.mError
					: String.format("Unknown error for opCode: %d", this.mRequestId);
	}
}
