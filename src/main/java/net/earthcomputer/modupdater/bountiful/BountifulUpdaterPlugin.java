package net.earthcomputer.modupdater.bountiful;

import java.util.Map;

import net.earthcomputer.modupdater.bountiful.transformers.TextureAtlasSpriteTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(2024)
public class BountifulUpdaterPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { TextureAtlasSpriteTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return BountifulUpdaterSetup.class.getName();
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
