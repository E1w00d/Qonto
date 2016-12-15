package com.chall.qonto.api;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef(flag=true,
		value={RequestType.NONE,
			   RequestType.USERS,
			   RequestType.ALBUMS,
			   RequestType.PHOTOS,})
@Retention(RetentionPolicy.SOURCE)
public @interface RequestType {
	int NONE = 0;
	int USERS  = 1;
	int ALBUMS = 1 << 1;
	int PHOTOS = 1 << 2;
}
