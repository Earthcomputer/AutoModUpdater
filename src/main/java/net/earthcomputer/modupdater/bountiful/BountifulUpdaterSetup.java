package net.earthcomputer.modupdater.bountiful;

import java.util.Map;

import com.google.common.base.Function;

import net.earthcomputer.modupdater.core.ClassAdapters;
import net.earthcomputer.modupdater.core.ClassNameReplacements;
import net.earthcomputer.modupdater.core.FieldAccess;
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
		ClassAdapters.registerClassAdapter("net.minecraft.util.Vec3",
				"net.earthcomputer.modupdater.bountiful.adapters.Vec3Adapter");

		FieldAccess.register("net.minecraft.util.Vec3", "xCoord", "field_72450_a", "getNonFinalXCoord",
				"setNonFinalXCoord");
		FieldAccess.register("net.minecraft.util.Vec3", "yCoord", "field_72448_b", "getNonFinalYCoord",
				"setNonFinalYCoord");
		FieldAccess.register("net.minecraft.util.Vec3", "zCoord", "field_72449_c", "getNonFinalZCoord",
				"setNonFinalZCoord");
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

}
