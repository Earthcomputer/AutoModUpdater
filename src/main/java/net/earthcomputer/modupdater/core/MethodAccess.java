package net.earthcomputer.modupdater.core;

import java.util.Map;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Maps;

import net.minecraftforge.classloading.FMLForgePlugin;

public class MethodAccess {

	private static final Map<Triple<String, String, String>, String> replacements = Maps.newHashMap();

	private MethodAccess() {
	}

	public static void register(String ownerName, String deobfName, String srgName, String desc, String newName) {
		replacements.put(Triple.of(ownerName, FMLForgePlugin.RUNTIME_DEOBF ? srgName : deobfName, desc), newName);
	}

	public static String getNewName(String ownerName, String methodName, String desc) {
		return replacements.get(Triple.of(ownerName, methodName, desc));
	}

}
