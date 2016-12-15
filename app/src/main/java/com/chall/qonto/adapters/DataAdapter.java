package com.chall.qonto.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chall.qonto.ui.Bindable;

public final class DataAdapter extends CursorRecyclerAdapter<DataAdapter.ViewHolder> {
	@LayoutRes
	private final int mLayoutId;

	private final View.OnClickListener mListener;

	public DataAdapter(final Context context,
					   @LayoutRes final int layoutId,
					   @Nullable final View.OnClickListener listener) {
		this(context, null, layoutId, listener);
	}

	private DataAdapter(final Context context,
						@Nullable final Cursor cursor,
						@LayoutRes final int layoutId,
						@Nullable final View.OnClickListener listener) {
		super(context, cursor);

		this.mLayoutId = layoutId;

		this.mListener = listener;
	}

	@Override
	public final DataAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		return new DataAdapter.ViewHolder(this.inflate(this.mLayoutId, parent, false), this.mListener);
	}

	@Override
	final void onBindViewHolder(final DataAdapter.ViewHolder holder, final Cursor cursor) {
		if (holder.itemView instanceof Bindable) {
			((Bindable)holder.itemView).onBind(cursor);
		}
	}

	static final class ViewHolder extends RecyclerView.ViewHolder {
		ViewHolder(final View itemView, final View.OnClickListener listener) {
			super(itemView);

			itemView.setOnClickListener(listener);
		}
	}
}
