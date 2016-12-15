package com.chall.qonto.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public final class NetworkUtils {
	private NetworkUtils() {}

	public static boolean hasNetworkConnection(final Context context) {
		final NetworkInfo activeNetworkInfo = NetworkUtils.getNetworkInfo(context);

		return null != activeNetworkInfo && activeNetworkInfo.isConnectedOrConnecting();
	}

	private static NetworkInfo getNetworkInfo(final Context context) {
		return ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
	}
}
