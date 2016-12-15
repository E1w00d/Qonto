package com.chall.qonto.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.chall.qonto.utils.ViewUtils;

public final class EsRecyclerView extends RecyclerView {
	private View mEmptyView;
	private View mSwipeView;
	private AdapterDataObserver mObserver;

	public EsRecyclerView(final Context context) {
		this(context, null);
	}

	public EsRecyclerView(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		this.addOnScrollListener(new OnScrollListener() {
			@Override
			public final void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
				if (null != EsRecyclerView.this.mSwipeView) {
					EsRecyclerView.this.mSwipeView.setEnabled(!ViewCompat.canScrollVertically(recyclerView, -1));
				}
			}
		});
	}

	public final void setEmptyView(@Nullable final View view) {
		ViewUtils.setVisibilitySafe(this.mEmptyView, View.GONE);

		this.mEmptyView = view;

		this.updateEmptyStatus();

		this.setupEmptyViewObserver(this.getAdapter());
	}

	public final void setSwipeRefreshView(@Nullable final View view) {
		this.mSwipeView = view;
	}

	@Override
	public final void setAdapter(final Adapter adapter) {
		if (null != this.mObserver && null != this.getAdapter()) {
			this.getAdapter().unregisterAdapterDataObserver(this.mObserver);

			this.mObserver = null;
		}

		super.setAdapter(adapter);

		this.setupEmptyViewObserver(adapter);

		this.updateEmptyStatus();
	}

	private void updateEmptyStatus() {
		final Adapter adapter = this.getAdapter();

		final boolean empty = null == adapter || 0 == adapter.getItemCount();

		ViewUtils.setVisibilitySafe(this.mEmptyView, empty ? View.VISIBLE : View.GONE);

		ViewUtils.setVisibilitySafe(this, empty ? View.GONE : View.VISIBLE);
	}

	private void setupEmptyViewObserver(final Adapter adapter) {
		if (null != adapter) {
			if (null != this.mObserver) {
				adapter.unregisterAdapterDataObserver(this.mObserver);

				this.mObserver = null;
			}

			adapter.registerAdapterDataObserver(this.mObserver = new Observer());
		}
	}

	private final class Observer extends AdapterDataObserver {
		@Override
		public final void onChanged() {
			EsRecyclerView.this.updateEmptyStatus();
		}
	}
}
