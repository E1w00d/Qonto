package com.chall.qonto.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.chall.qonto.utils.Utils;
import com.chall.qonto.utils.ViewUtils;

public final class AlbumView extends FrameLayout implements Bindable, Recyclable {
	private long mAlbumId;

	private TextView mTitle;
	private ImageView mCover;

	public AlbumView(final Context context) {
		this(context, null);
	}

	public AlbumView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public final long getAlbumId() {
		return this.mAlbumId;
	}

	@Override
	protected final void onFinishInflate() {
		super.onFinishInflate();

		this.mCover = ViewUtils.getView(this, android.R.id.icon);

		this.mTitle = ViewUtils.getView(this, android.R.id.text1);
	}

	@Override
	public final void onRecycle() {
		this.mAlbumId = 0L;

		this.mTitle.setText(null);
	}

	@Override
	public final void onBind(@NonNull final Cursor data) {
		this.mAlbumId = data.getLong(1);

		this.mTitle.setText(data.getString(2));

		String c = String.valueOf(this.mTitle.getText().charAt(0));

		this.mCover.setImageDrawable(TextDrawable.builder()
												 .beginConfig()
												 .toUpperCase()
												 .withBorder(Utils.toPx(this.getContext(), 2))
												 .endConfig()
												 .buildRound(c, ColorGenerator.MATERIAL.getColor(c)));
	}
}
