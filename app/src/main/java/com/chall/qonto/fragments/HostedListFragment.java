package com.chall.qonto.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chall.qonto.R;
import com.chall.qonto.api.RequestType;
import com.chall.qonto.api.ResponseEvent;
import com.chall.qonto.ui.EsRecyclerView;
import com.chall.qonto.utils.EventBusModule;
import com.chall.qonto.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

abstract class HostedListFragment extends HostedFragment implements View.OnClickListener,
																	SwipeRefreshLayout.OnRefreshListener {
	RecyclerView.Adapter mAdapter;

	EsRecyclerView mRecyclerView;
	SwipeRefreshLayout mSwipeRefreshLayout;

	abstract RecyclerView.Adapter getAdapter();

	@Override
	final int getContentViewId() {
		return R.layout.generic_reviews;
	}

	@NonNull
	RecyclerView.ItemDecoration getItemDecoration() {
		return new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
	}

	@Override
	public final void onAttach(final Context context) {
		super.onAttach(context);

		EventBusModule.register(this);
	}

	@Override
	public final void onDetach() {
		EventBusModule.unregister(this);

		super.onDetach();
	}

	@CallSuper
	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.mSwipeRefreshLayout = ViewUtils.getView(view, R.id.swipe_refresh);

		this.mSwipeRefreshLayout.setOnRefreshListener(this);
		this.mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

		this.mRecyclerView = ViewUtils.getView(view, android.R.id.list);

		this.mRecyclerView.addItemDecoration(this.getItemDecoration());

		this.mRecyclerView.setSwipeRefreshView(this.mSwipeRefreshLayout);
		this.mRecyclerView.setEmptyView(ViewUtils.getViewOrNull(this.getView(), R.id.loading_indicator));

		this.mRecyclerView.setHasFixedSize(true);
		this.mRecyclerView.setAdapter(this.mAdapter = this.getAdapter());
	}

	@Override
	public final boolean isEmpty() {
		return 0 == this.mAdapter.getItemCount();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public final void handleResponse(@NonNull final ResponseEvent event) {
		switch (event.getRequestId()) {
			case RequestType.USERS:
			case RequestType.ALBUMS:
			case RequestType.PHOTOS:
				if (null != this.mSwipeRefreshLayout) {
					this.mSwipeRefreshLayout.setRefreshing(false);
				}
				break;
			default:
				break;
		}
	}
}
