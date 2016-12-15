package com.chall.qonto.utils;

import java.util.Arrays;

public final class ArrayUtils {
	private ArrayUtils() {}

	public static boolean isEmpty(final int[] array) {
		return null == array || 0 == array.length;
	}

	public static boolean isEmpty(final byte[] array) {
		return null == array || 0 == array.length;
	}

	public static boolean isEmpty(final String[] array) {
		return null == array || 0 == array.length;
	}

	public static boolean isEmpty(final Object[] array) {
		return null == array || 0 == array.length;
	}

	public static String[] prependStrings(final String[] oldArgList, final String... args) {
		return ArrayUtils.concat(args, oldArgList);
	}

	public static <T> T[] concat(final T[] first, final T[] second) {
		if (ArrayUtils.isEmpty(first)) {
			return second;
		}

		if (ArrayUtils.isEmpty(second)) {
			return first;
		}

		final int firstLen  = first.length;
		final int secondLen = second.length;

		final T[] result = Arrays.copyOf(first, firstLen + secondLen);

		System.arraycopy(second, 0, result, firstLen, secondLen);

		return result;
	}
}
