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

public final class UserView extends FrameLayout implements Bindable, Recyclable {
	private long mUserId;

	private ImageView mAvatar;
	private TextView mUserName;

	public UserView(final Context context) {
		this(context, null);
	}

	public UserView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	public final long getUserId() {
		return this.mUserId;
	}

	@Override
	protected final void onFinishInflate() {
		super.onFinishInflate();

		this.mAvatar = ViewUtils.getView(this, android.R.id.icon);

		this.mUserName = ViewUtils.getView(this, android.R.id.text1);
	}

	@Override
	public final void onRecycle() {
		this.mUserId = 0L;

		this.mUserName.setText(null);
	}

	@Override
	public final void onBind(@NonNull final Cursor data) {
		this.mUserId = data.getLong(1);

		this.mUserName.setText(data.getString(2));

		String c = String.valueOf(this.mUserName.getText().charAt(0));

		this.mAvatar.setImageDrawable(TextDrawable.builder()
												  .beginConfig()
												  .toUpperCase()
												  .withBorder(Utils.toPx(this.getContext(), 2))
												  .endConfig()
												  .buildRound(c, ColorGenerator.MATERIAL.getColor(c)));
	}
}
