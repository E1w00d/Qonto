package com.chall.qonto.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chall.qonto.utils.ViewUtils;

public final class PhotoView extends FrameLayout implements Bindable, Recyclable {
	private long mPhotoId;

	private String mUrl;

	private TextView mTitle;
	private ImageView mThumb;

	public PhotoView(final Context context) {
		this(context, null);
	}

	public PhotoView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public final long getPhotoId() {
		return this.mPhotoId;
	}

	public final String getUrl() {
		return this.mUrl;
	}

	@Override
	protected final void onFinishInflate() {
		super.onFinishInflate();

		this.mThumb = ViewUtils.getView(this, android.R.id.icon);

		this.mTitle = ViewUtils.getView(this, android.R.id.text1);
	}

	@Override
	public final void onRecycle() {
		this.mPhotoId = 0L;

		this.mTitle.setText(this.mUrl = null);
	}

	@Override
	public final void onBind(@NonNull final Cursor data) {
		this.mPhotoId = data.getLong(1);

		this.mUrl = data.getString(2);

		this.mTitle.setText(data.getString(3));

		Glide.with(this.getContext())
			 .load(data.getString(4))
			 .crossFade()
			 .centerCrop()
			 .into(this.mThumb);
	}
}
