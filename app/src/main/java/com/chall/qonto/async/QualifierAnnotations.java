package com.chall.qonto.async;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class QualifierAnnotations {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Parallel {}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Sequential {}
}
