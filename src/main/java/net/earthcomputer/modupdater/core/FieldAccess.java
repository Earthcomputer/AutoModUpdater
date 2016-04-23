package net.earthcomputer.modupdater.core;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import net.minecraftforge.classloading.FMLForgePlugin;

public class FieldAccess {

	private static final Map<Pair<String, String>, String> getters = Maps.newHashMap();

	private static final Map<Pair<String, String>, String> setters = Maps.newHashMap();

	private FieldAccess() {
	}

	public static void register(String owner, String deobfName, String srgName, String getter, String setter) {
		Pair<String, String> pair = Pair.of(owner, FMLForgePlugin.RUNTIME_DEOBF ? srgName : deobfName);
		getters.put(pair, getter);
		setters.put(pair, setter);
	}

	public static String getGetter(String owner, String fieldName) {
		return getters.get(Pair.of(owner, fieldName));
	}

	public static String getSetter(String owner, String fieldName) {
		return setters.get(Pair.of(owner, fieldName));
	}

}
