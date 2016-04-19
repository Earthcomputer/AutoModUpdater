package net.earthcomputer.modupdater.core;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ClassNameReplacements {

	private static final List<Function<String, String>> replacements = Lists.newArrayList();

	private ClassNameReplacements() {
	}

	public static void replace(final String from, final String to) {
		replace(new Function<String, String>() {
			@Override
			public String apply(String input) {
				return input.equals(from) ? to : input;
			}
		});
	}

	public static void replace(Function<String, String> replacementFunction) {
		replacements.add(replacementFunction);
	}

	public static String getReplacement(String className) {
		for (Function<String, String> replacement : replacements) {
			className = replacement.apply(className);
		}
		return className;
	}

}
