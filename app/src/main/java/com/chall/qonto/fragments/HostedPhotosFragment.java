package com.chall.qonto.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chall.qonto.R;
import com.chall.qonto.api.RequestType;
import com.chall.qonto.api.RestClient;
import com.chall.qonto.data.DatabaseProvider;
import com.chall.qonto.ui.SpacesItemDecoration;

public final class HostedPhotosFragment extends HostedAlbumsFragment {
	public static final class Builder extends HostedFragment.Builder<HostedUsersFragment.Builder> {
		public final Builder setAlbumId(final long albumId) {
			this.mArguments.putLong("album_id", albumId);
			return this;
		}

		@Override
		public final Fragment build() {
			final Fragment fragment = new HostedPhotosFragment();

			this.configureFragment(fragment);

			return fragment;
		}
	}

	private long mAlbumId;

	@Override
	final void onSetArguments(@Nullable final Bundle args) {
		super.onSetArguments(args);

		this.mAlbumId = null == args ? 0L : args.getLong("album_id");
	}

	@Override
	final int getItemLayoutId() {
		return R.layout.photo_item;
	}

	@NonNull
	@Override
	final RecyclerView.ItemDecoration getItemDecoration() {
		return new SpacesItemDecoration(4);
	}

	@Override
	public final void onDestroyView() {
		super.onDestroyView();

		Glide.get(this.getContext()).clearMemory();
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
	}

	@Override
	public final Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		return new CursorLoader(this.getContext(), DatabaseProvider.buildPhotoUri(this.mAlbumId),
								new String[] {"_id", "photo_id", "url", "title", "thumbnail",}, null, null, null);
	}

	@Override
	public final void onRefresh() {
		final Bundle args = new Bundle(2);

		args.putInt("req_id", RequestType.PHOTOS);
		args.putLong("album_id", this.mAlbumId);

		RestClient.getInstance().handleRequest(args);
	}
}
