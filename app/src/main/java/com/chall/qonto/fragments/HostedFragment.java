package com.chall.qonto.fragments;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public abstract class HostedFragment extends Fragment {
	@SuppressWarnings("unchecked")
	public static abstract class Builder<T extends Builder> {
		public abstract Fragment build();

		final Bundle mArguments = new Bundle(2);

		public final T setTitle(final String title) {
			this.mArguments.putString("title", title);
			return (T)this;
		}

		public final T setTitleId(@StringRes final int resId) {
			this.mArguments.putInt("title_id", resId);
			return (T)this;
		}

		final void configureFragment(@NonNull final Fragment fragment) {
			fragment.setArguments(this.mArguments);
		}
	}

	@LayoutRes
	abstract int getContentViewId();

	@Override
	public void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.onSetArguments(this.getArguments());

		this.setHasOptionsMenu(this.hasMenu());
	}

	@Override
	public final void onActivityCreated(@Nullable final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.loadContent();
	}

	@Nullable
	@Override
	public final View onCreateView(final LayoutInflater inflater,
								   final ViewGroup container,
								   final Bundle savedInstanceState) {
		try {
			return inflater.inflate(this.getContentViewId(), container, false);
		}
		catch (final InflateException ex) {
			this.getActivity().finish();

			Timber.wtf(ex, "Failed to inflate the view %d", this.getContentViewId());
		}
		catch (final OutOfMemoryError ex) {
			System.gc();

			this.getActivity().finish();

			Timber.wtf(ex, "Inflate the view %d threw an OOME", this.getContentViewId());
		}
		catch (final Throwable ex) {
			Timber.wtf(ex, "Failed to inflate the view %d", this.getContentViewId());

			throw new RuntimeException(ex);
		}

		return null;
	}

	boolean hasMenu() {
		return false;
	}

	public boolean isEmpty() {
		return true;
	}

	public void loadContent() {}

	@CallSuper
	void onSetArguments(@Nullable final Bundle args) {}
}
