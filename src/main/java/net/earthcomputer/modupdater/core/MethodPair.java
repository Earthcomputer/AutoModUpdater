package net.earthcomputer.modupdater.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodPair {
	String name();

	EnumType type();

	public static enum EnumType {
		OLDER, NEWER
	}
}
