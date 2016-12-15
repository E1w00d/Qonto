package com.chall.qonto.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.chall.qonto.R;
import com.chall.qonto.api.RequestType;
import com.chall.qonto.api.RestClient;
import com.chall.qonto.data.DatabaseProvider;

public class HostedAlbumsFragment extends HostedUsersFragment {
	public static final class Builder extends HostedFragment.Builder<HostedUsersFragment.Builder> {
		public final Builder setUserId(final long userId) {
			this.mArguments.putLong("user_id", userId);
			return this;
		}

		@Override
		public final Fragment build() {
			final Fragment fragment = new HostedAlbumsFragment();

			this.configureFragment(fragment);

			return fragment;
		}
	}

	long mUserId;

	@Override
	void onSetArguments(@Nullable final Bundle args) {
		super.onSetArguments(args);

		this.mUserId = null == args ? 0L : args.getLong("user_id");
	}

	@Override
	int getItemLayoutId() {
		return R.layout.album_item;
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		return new CursorLoader(this.getContext(), DatabaseProvider.buildAlbumUri(this.mUserId),
								new String[] {"_id", "album_id", "title",}, null, null, null);
	}

	@Override
	public void onRefresh() {
		final Bundle args = new Bundle(2);

		args.putInt("req_id", RequestType.ALBUMS);
		args.putLong("user_id", this.mUserId);

		RestClient.getInstance().handleRequest(args);
	}
}
