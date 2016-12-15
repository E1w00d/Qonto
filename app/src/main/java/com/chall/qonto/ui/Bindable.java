package com.chall.qonto.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;

public interface Bindable {
	void onBind(@NonNull final Cursor data);
}
