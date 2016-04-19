package net.earthcomputer.modupdater.bountiful;

import java.util.Map;

import com.google.common.base.Function;

import net.earthcomputer.modupdater.core.ClassAdapters;
import net.earthcomputer.modupdater.core.ClassNameReplacements;
import net.minecraftforge.fml.relauncher.IFMLCallHook;

public class BountifulUpdaterSetup implements IFMLCallHook {

	@Override
	public Void call() throws Exception {
		ClassNameReplacements.replace(new Function<String, String>() {
			@Override
			public String apply(String input) {
				return input.startsWith("cpw.mods.fml.") ? "net.minecraftforge" + input.substring(8) : input;
			}
		});
		ClassNameReplacements.replace("net.minecraft.util.IIcon",
				"net.earthcomputer.modupdater.bountiful.oldclasses.IIcon");
		ClassAdapters.registerClassAdapter("net.minecraft.block.Block",
				"net.earthcomputer.modupdater.bountiful.adapters.BlockAdapter");
		ClassAdapters.registerClassAdapter("net.minecraft.block.Block",
				"net.earthcomputer.modupdater.bountiful.adapters.BlockAdapter2");
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

}
