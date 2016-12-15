package com.chall.qonto.utils;

import android.text.TextUtils;

public final class Preconditions {
	private Preconditions() {}

	public static <T> T checkNotNull(final T reference) {
		if (null == reference) {
			throw new NullPointerException();
		}

		return reference;
	}

	public static <T> T checkNotNull(final T reference, final Object errorMessage) {
		if (null == reference) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}

		return reference;
	}

	public static <T extends CharSequence> T checkNotEmpty(final T charSequence, final Object errorMessage) {
		if (TextUtils.isEmpty(charSequence)) {
			throw new IllegalArgumentException(String.valueOf(errorMessage));
		}

		return charSequence;
	}

	public static void checkArgument(final boolean expression, final String errorMessageTemplate, final Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalArgumentException(Preconditions.format(errorMessageTemplate, errorMessageArgs));
		}
	}

	private static String format(final String template, final Object... args) {
		final StringBuilder sb = StringUtils.acquireStringBuilder();

		try {
			int templateStart = 0;
			int i = 0, i2;

			while (i < args.length) {
				int placeholderStart = template.indexOf("%s", templateStart);

				if (placeholderStart == -1) {
					break;
				}

				sb.append(template.substring(templateStart, placeholderStart));

				i2 = i + 1;

				sb.append(args[i]);
				templateStart = placeholderStart + 2;

				i = i2;
			}

			sb.append(template.substring(templateStart));

			if (i < args.length) {
				sb.append(" [");

				i2 = i + 1;

				sb.append(args[i]);

				i = i2;

				while (i < args.length) {
					sb.append(", ");

					i2 = i + 1;

					sb.append(args[i]);

					i = i2;
				}

				sb.append(']');
			}

			return sb.toString();
		}
		finally {
			StringUtils.releaseStringBuilder(sb);
		}
	}
}
