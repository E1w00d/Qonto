package com.chall.qonto.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.chall.qonto.R;
import com.chall.qonto.api.RequestType;
import com.chall.qonto.api.RestClient;
import com.chall.qonto.fragments.HostedAlbumsFragment;
import com.chall.qonto.fragments.HostedFragment;
import com.chall.qonto.fragments.HostedPhotosFragment;
import com.chall.qonto.fragments.HostedUsersFragment;

public final class HostActivity extends BaseActivity {
	HostedFragment mHostedFragment;

	@Override
	public final void onAttachFragment(final Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment instanceof HostedFragment) {
			this.mHostedFragment = (HostedFragment)fragment;
		}
	}

	@Override
	public final void initActionBar(@Nullable final ActionBar actionBar) {
		super.initActionBar(actionBar);

		if (null != actionBar) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	final void handleIntent(final Intent intent) {
		super.handleIntent(intent);

		final Bundle args = new Bundle(2);

		if (intent.hasExtra("user_id")) {
			args.putInt("req_id", RequestType.ALBUMS);

			args.putLong("user_id", intent.getLongExtra("user_id", 0L));

			this.replaceFragment(new HostedAlbumsFragment.Builder()
									 					 .setUserId(intent.getLongExtra("user_id", 0L))
														 .build());
		}
		else if (intent.hasExtra("album_id")) {
			args.putInt("req_id", RequestType.PHOTOS);

			args.putLong("album_id", intent.getLongExtra("album_id", 0L));

			this.replaceFragment(new HostedPhotosFragment.Builder()
									 					 .setAlbumId(intent.getLongExtra("album_id", 0L))
														 .build());
		}
		else {
			args.putInt("req_id", RequestType.USERS);

			this.replaceFragment(new HostedUsersFragment.Builder().build());
		}

		RestClient.getInstance().handleRequest(args);
	}

	@Override
	final int getLayoutId() {
		return R.layout.host_activity;
	}

	@Override
	final int getDefaultFragmentContainerViewId() {
		return R.id.hosted_content;
	}
}
