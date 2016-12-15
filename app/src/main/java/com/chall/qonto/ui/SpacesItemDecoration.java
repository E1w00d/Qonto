package com.chall.qonto.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public final class SpacesItemDecoration extends RecyclerView.ItemDecoration {
	private final int mSpace;

	public SpacesItemDecoration(final Context context, @DimenRes final int resId) {
		this(context.getResources().getDimensionPixelSize(resId));
	}

	public SpacesItemDecoration(final int space) {
		this.mSpace = space;
	}

	@Override
	public final void getItemOffsets(final Rect outRect, final View view,
									 final RecyclerView parent, final RecyclerView.State state) {
		outRect.left = this.mSpace;
		outRect.right = this.mSpace;
		outRect.bottom = this.mSpace;

		// Add top margin only for the first item to avoid double space between items
		if (0 == parent.getChildAdapterPosition(view)) {
			outRect.top = this.mSpace;
		}
	}
}
