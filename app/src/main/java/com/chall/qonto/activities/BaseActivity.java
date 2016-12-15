package com.chall.qonto.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chall.qonto.R;
import com.chall.qonto.api.ResponseEvent;
import com.chall.qonto.utils.EventBusModule;
import com.chall.qonto.utils.ViewUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public abstract class BaseActivity  extends AppCompatActivity {
	@LayoutRes
	abstract int getLayoutId();

	@TargetApi(21)
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		try {
			this.setResult(Activity.RESULT_CANCELED);

			if (null == savedInstanceState) {
				this.logIntentExtras("onCreate");
			}
			else {
				savedInstanceState.setClassLoader(this.getClass().getClassLoader());
			}

			super.onCreate(savedInstanceState);

			this.setContentView(this.getLayoutId());

			this.handleIntent(this.getIntent());

			final Toolbar toolbar = ViewUtils.getViewOrNull(this, R.id.toolbar);

			if (null != toolbar) {
				this.setSupportActionBar(toolbar);
			}

			this.initActionBar(this.getSupportActionBar());
		}
		catch (final Throwable ex) {
			Timber.e(ex, "Failed to start activity: %s", this.getClass().getSimpleName());

			this.finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		EventBusModule.register(this);
	}

	@Override
	protected void onPause() {
		EventBusModule.unregister(this);

		super.onPause();
	}

	@Override
	protected final void onNewIntent(final Intent intent) {
		super.onNewIntent(intent);

		this.handleIntent(intent);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public final void replaceFragment(@NonNull final Fragment fragment) {
		final FragmentManager fm = this.getSupportFragmentManager();

		fm.beginTransaction()
		  .replace(this.getDefaultFragmentContainerViewId(), fragment, "hosted")
		  .setTransition(FragmentTransaction.TRANSIT_NONE)
		  .commit();
	}

	@IdRes
	int getDefaultFragmentContainerViewId() {
		return android.R.id.content;
	}

	public void initActionBar(@Nullable final ActionBar actionBar) {}

	@CallSuper
	void handleIntent(final Intent intent) {
		this.setIntent(intent);

		if (intent.getBooleanExtra("exit", false)) {
			this.finish();
			this.overridePendingTransition(0, 0);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public final void handleResponse(@NonNull final ResponseEvent event) {
		if (event.hasError()) {
			final View child = ViewUtils.getViewOrNull(this, this.getDefaultFragmentContainerViewId());

			if (null != child) {
				Snackbar.make(child, event.getErrorMessage(), Snackbar.LENGTH_LONG).show();
			}

			Timber.wtf("Request with opCode %d has failed: %s", event.getRequestId(), event.getErrorMessage());
		}
	}

	private void logIntentExtras(final String tag) {
		try {
			final Intent intent = this.getIntent();

			Timber.v("%s: %s  %s", tag, this.getClass().getSimpleName(), intent);

			this.logBundle("  Extra", intent.getExtras());
		}
		catch (final Exception ex) {
			Timber.wtf(ex, "Failed to log intent");
		}
	}

	private void logBundle(final String tag, final Bundle data) {
		if (null != data && 0 < data.size()) {
			final List<String> list = new ArrayList<>(data.keySet());

			Collections.sort(list);

			for (final String key : list) {
				Timber.v("%s: %s = %s", tag, key, data.get(key));
			}
		}
	}
}
