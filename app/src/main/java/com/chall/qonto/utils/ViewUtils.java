package com.chall.qonto.utils;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;

@SuppressWarnings({"unchecked", "TypeParameterUnusedInFormals"})
public final class ViewUtils {
	private ViewUtils() {}

	public static <T extends View> T getViewOrNull(final Activity parent, @IdRes final int viewId) {
		return null == parent ? null : (T)parent.findViewById(viewId);
	}

	public static <T extends View> T getViewOrNull(final View parent, @IdRes final int viewId) {
		return null == parent ? null : (T)parent.findViewById(viewId);
	}

	public static <T extends View> T getView(final Activity parent, @IdRes final int viewId) {
		return (T)ViewUtils.checkView(null == parent ? null : parent.findViewById(viewId));
	}

	public static <T extends View> T getView(final View parent, @IdRes final int viewId) {
		return (T)ViewUtils.checkView(null == parent ? null : parent.findViewById(viewId));
	}

	public static void setVisibilitySafe(final View view, final int visibility) {
		if (null != view && visibility != view.getVisibility()) {
			view.setVisibility(visibility);
		}
	}

	private static View checkView(final View view) {
		return Preconditions.checkNotNull(view);
	}
}
