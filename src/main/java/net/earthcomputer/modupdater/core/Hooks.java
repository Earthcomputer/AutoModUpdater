package net.earthcomputer.modupdater.core;

import java.util.List;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.Restriction;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class Hooks {

	private Hooks() {
	}

	public static void onPreSortModList() {
		ModUpdaterPlugin.LOGGER.info("Searching for mods which do not accept this version of Minecraft");

		List<ModContainer> modContainers = Loader.instance().getActiveModList();
		ArtifactVersion mcVersion = Loader.instance().getMinecraftModContainer().getProcessedVersion();
		final VersionRange mcVersionRange = Loader.instance().getMinecraftModContainer().getStaticVersionRange();

		for (int i = 0; i < modContainers.size(); i++) {
			ModContainer mod = modContainers.get(i);
			VersionRange acceptableVersionRange = mod.acceptableMinecraftVersionRange();

			if (!acceptableVersionRange.containsVersion(mcVersion)) {
				boolean canHaveOlder = false;
				for (Restriction restriction : acceptableVersionRange.getRestrictions()) {
					ArtifactVersion upperBound = restriction.getUpperBound();
					if (upperBound.compareTo(mcVersion) < 0) {
						canHaveOlder = true;
						break;
					}
				}
				if (canHaveOlder) {
					ModUpdaterPlugin.LOGGER.info(
							String.format("%s only accepts Minecraft versions %s, but ModUpdater will sort that out!",
									mod.getName(), acceptableVersionRange.toString()));
					modContainers.set(i, new CopyModContainer(mod) {
						@Override
						public VersionRange acceptableMinecraftVersionRange() {
							return mcVersionRange;
						}
					});
				}
			}
		}
	}

}
