package com.chall.qonto.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import okhttp3.internal.Util;

public abstract class CursorRecyclerAdapter <T extends RecyclerView.ViewHolder> extends RecyclerAdapter<T> {
	private Cursor mCursor;

	private int mRowIdColumn;

	private boolean mDataValid;

	private final DataSetObserver mDataSetObserver = new DataSetObserver () {
		@Override
		public final void onChanged() {
			super.onChanged();

			CursorRecyclerAdapter.this.mDataValid = true;
			CursorRecyclerAdapter.this.notifyDataSetChanged();
		}

		@Override
		public final void onInvalidated() {
			super.onInvalidated();

			CursorRecyclerAdapter.this.mDataValid = false;
			CursorRecyclerAdapter.this.notifyDataSetChanged();
		}
	};

	abstract void onBindViewHolder(final T holder, final Cursor cursor);

	CursorRecyclerAdapter(final Context context, @Nullable final Cursor cursor) {
		super(context);

		if (this.mDataValid = null != (this.mCursor = cursor)) {
			this.mCursor.registerDataSetObserver(this.mDataSetObserver);
		}

		this.mRowIdColumn = this.mDataValid ? cursor.getColumnIndex(BaseColumns._ID) : -1;
	}

	public final void changeCursor(@Nullable final Cursor cursor) {
		Util.closeQuietly(this.swapCursor(cursor));
	}

	public final Cursor swapCursor(@Nullable final Cursor cursor) {
		if (cursor != this.mCursor) {
			final Cursor oldCursor = this.mCursor;

			if (null != oldCursor && null != this.mDataSetObserver) {
				oldCursor.unregisterDataSetObserver(this.mDataSetObserver);
			}

			if (this.mDataValid = null != (this.mCursor = cursor)) {
				if (null != this.mDataSetObserver) {
					this.mCursor.registerDataSetObserver(this.mDataSetObserver);
				}

				this.mRowIdColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID);
			}
			else {
				this.mRowIdColumn = -1;
			}

			this.notifyDataSetChanged();

			return oldCursor;
		}

		return null;
	}

	public final boolean isEmpty() {
		return 0 >= this.getItemCount();
	}

	@Override
	public int getItemCount() {
		return this.mDataValid && null != this.mCursor ? this.mCursor.getCount() : 0;
	}

	@Override
	public final long getItemId(final int position) {
		if (this.mDataValid && null != this.mCursor && this.mCursor.moveToPosition(position)) {
			return this.mCursor.getLong(this.mRowIdColumn);
		}

		return super.getItemId(position);
	}

	@Override
	public final void setHasStableIds(final boolean hasStableIds) {
		super.setHasStableIds(true);
	}

	@Override
	public void onBindViewHolder(final T holder, final int position) {
		if (!this.mDataValid) {
			throw new IllegalStateException("This should only be called when the cursor is valid");
		}

		if (!this.mCursor.moveToPosition(position)) {
			throw new IllegalStateException(String.format("Could not move cursor to position %d", position));
		}

		this.onBindViewHolder(holder, this.mCursor);
	}
}
