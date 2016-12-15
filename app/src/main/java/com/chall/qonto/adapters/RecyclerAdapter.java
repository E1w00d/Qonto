package com.chall.qonto.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chall.qonto.utils.Utils;

abstract class RecyclerAdapter <T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
	private final LayoutInflater mInflater;

	RecyclerAdapter(final Context context) {
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public final void onViewRecycled(final T holder) {
		Utils.recycleAll(holder.itemView);
	}

	final View inflate(@LayoutRes final int resource,
					   @Nullable final ViewGroup root,
					   final boolean attachToRoot) {
		return this.mInflater.inflate(resource, root, attachToRoot);
	}
}
