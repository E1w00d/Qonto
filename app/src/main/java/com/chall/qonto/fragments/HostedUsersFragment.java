package com.chall.qonto.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.StaleDataException;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chall.qonto.R;
import com.chall.qonto.activities.HostActivity;
import com.chall.qonto.adapters.CursorRecyclerAdapter;
import com.chall.qonto.adapters.DataAdapter;
import com.chall.qonto.api.RequestType;
import com.chall.qonto.api.RestClient;
import com.chall.qonto.data.DatabaseProvider;
import com.chall.qonto.ui.AlbumView;
import com.chall.qonto.ui.PhotoView;
import com.chall.qonto.ui.UserView;
import com.chall.qonto.utils.Utils;
import com.chall.qonto.utils.ViewUtils;

import timber.log.Timber;

public class HostedUsersFragment extends HostedListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	public static final class Builder extends HostedFragment.Builder<Builder> {
		@Override
		public final Fragment build() {
			final Fragment fragment = new HostedUsersFragment();

			this.configureFragment(fragment);

			return fragment;
		}
	}

	@NonNull
	@Override
	final RecyclerView.Adapter getAdapter() {
		return new DataAdapter(this.getContext(), this.getItemLayoutId(), this);
	}

	@Override
	public final void loadContent() {
		this.getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		return new CursorLoader(this.getContext(), DatabaseProvider.USER_URI,
								new String[] {"_id", "user_id", "full_name",}, null, null, null);
	}

	@Override
	public final void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
		try {
			if (this.mAdapter instanceof CursorRecyclerAdapter) {
				((CursorRecyclerAdapter)this.mAdapter).changeCursor(data);
			}
		}
		catch (final StaleDataException ex) {
			Timber.wtf(ex, "Failed to handle cursor loader result");
		}
		finally {
			this.mRecyclerView.setEmptyView(ViewUtils.getViewOrNull(this.getView(), R.id.no_results_view));
		}
	}

	@Override
	public final void onLoaderReset(final Loader<Cursor> loader) {
		if (this.mAdapter instanceof CursorRecyclerAdapter) {
			((CursorRecyclerAdapter)this.mAdapter).changeCursor(null);
		}
	}

	@LayoutRes
	int getItemLayoutId() {
		return R.layout.user_item;
	}

	@Override
	public void onRefresh() {
		final Bundle args = new Bundle(2);

		args.putInt("req_id", RequestType.USERS);

		RestClient.getInstance().handleRequest(args);
	}

	@Override
	public final void onClick(final View view) {
		final Intent intent = new Intent(this.getContext(), HostActivity.class);

		if (view instanceof UserView) {
			this.startActivity(intent.putExtra("user_id", ((UserView)view).getUserId()));
		}
		else if (view instanceof AlbumView) {
			this.startActivity(intent.putExtra("album_id", ((AlbumView)view).getAlbumId()));
		}
		else if (view instanceof PhotoView) {
			this.startActivity(new Intent(Intent.ACTION_VIEW,
										  Utils.parseUri(((PhotoView)view).getUrl())));
		}
	}
}
