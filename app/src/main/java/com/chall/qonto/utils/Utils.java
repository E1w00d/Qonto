package com.chall.qonto.utils;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.chall.qonto.ui.Recyclable;

import timber.log.Timber;

public final class Utils {
	private Utils() {}

	public static int elapsedRealtime() {
		return (int)(SystemClock.elapsedRealtime() % Integer.MAX_VALUE);
	}

	public static Uri parseUri(final String uri) {
		return TextUtils.isEmpty(uri) ? Uri.EMPTY : Uri.parse(uri);
	}

	public static double parseDouble(final CharSequence str, final double defaultValue) {
		if (!TextUtils.isEmpty(str)) {
			try {
				return Double.parseDouble((String)str);
			}
			catch (final NumberFormatException ex) {
				Timber.d("Failed to parseDouble: %s", str);
			}
		}

		return defaultValue;
	}

	public static void recycleAll(@Nullable final View view) {
		if (null != view) {
			Utils.recycleAllChildren(view);

			if (view instanceof Recyclable) {
				((Recyclable)view).onRecycle();
			}
		}
	}

	public static void recycleAllChildren(@Nullable final View view) {
		if (view instanceof ViewGroup) {
			final ViewGroup parent = (ViewGroup)view;

			//noinspection StatementWithEmptyBody
			for (int idx = parent.getChildCount(); --idx >= 0;
				 Utils.recycleAll(parent.getChildAt(idx)));
		}
	}

	public static int toPx(final Context context, @IntRange(from = 1, to = 12) final int dp) {
		return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
											  context.getResources().getDisplayMetrics());
	}
}
