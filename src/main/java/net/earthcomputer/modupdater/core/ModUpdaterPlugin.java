package net.earthcomputer.modupdater.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.earthcomputer.modupdater.core.transformers.ClassNameReplacerTransformer;
import net.earthcomputer.modupdater.core.transformers.FieldAccessTransformer;
import net.earthcomputer.modupdater.core.transformers.AdapterClassTransformer;
import net.earthcomputer.modupdater.core.transformers.ModLoadingTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

/**
 * 
 * Clients should use the following sorting indexes for Minecraft versions: 1.1:
 * 2001 1.2.3: 2002 1.2.4: 2003 1.2.5: 2004 1.3.2: 2005 1.4.0: 2006 1.4.1: 2007
 * 1.4.2: 2008 1.4.3: 2009 1.4.4: 2010 1.4.5: 2011 1.4.6: 2012 1.4.7: 2013 1.5:
 * 2014 1.5.1: 2015 1.5.2: 2016 1.6.1: 2017 1.6.2: 2018 1.6.3: 2019 1.6.4: 2020
 * 1.7.2: 2021 1.7.10_pre4: 2022 1.7.10: 2023 1.8: 2024 1.8.8: 2025 1.8.9: 2026
 * 1.9: 2027
 *
 */
@SortingIndex(1001)
@TransformerExclusions({ "net.earthcomputer.modupdater.", "net.minecraftforge.gradle." })
public class ModUpdaterPlugin implements IFMLLoadingPlugin {

	public static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("modupdater_debug", "false"));
	public static final Logger LOGGER = LogManager.getLogger("ModUpdater");

	public static void debug(String message) {
		if (DEBUG)
			LOGGER.info(message);
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { ClassNameReplacerTransformer.class.getName(), ModLoadingTransformer.class.getName(),
				AdapterClassTransformer.class.getName(), FieldAccessTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
