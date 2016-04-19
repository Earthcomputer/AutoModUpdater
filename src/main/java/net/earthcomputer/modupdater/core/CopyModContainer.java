package net.earthcomputer.modupdater.core;

import java.io.File;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionRange;

public class CopyModContainer implements ModContainer {

	private ModContainer mod;
	
	public CopyModContainer(ModContainer mod) {
		this.mod = mod;
	}
	
	@Override
	public String getModId() {
		return mod.getModId();
	}

	@Override
	public String getName() {
		return mod.getName();
	}

	@Override
	public String getVersion() {
		return mod.getVersion();
	}

	@Override
	public File getSource() {
		return mod.getSource();
	}

	@Override
	public ModMetadata getMetadata() {
		return mod.getMetadata();
	}

	@Override
	public void bindMetadata(MetadataCollection mc) {
		mod.bindMetadata(mc);
	}

	@Override
	public void setEnabledState(boolean enabled) {
		mod.setEnabledState(enabled);
	}

	@Override
	public Set<ArtifactVersion> getRequirements() {
		return mod.getRequirements();
	}

	@Override
	public List<ArtifactVersion> getDependencies() {
		return mod.getDependencies();
	}

	@Override
	public List<ArtifactVersion> getDependants() {
		return mod.getDependants();
	}

	@Override
	public String getSortingRules() {
		return mod.getSortingRules();
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		return mod.registerBus(bus, controller);
	}

	@Override
	public boolean matches(Object mod) {
		return this.mod.matches(mod);
	}

	@Override
	public Object getMod() {
		return mod.getMod();
	}

	@Override
	public ArtifactVersion getProcessedVersion() {
		return mod.getProcessedVersion();
	}

	@Override
	public boolean isImmutable() {
		return mod.isImmutable();
	}

	@Override
	public String getDisplayVersion() {
		return mod.getDisplayVersion();
	}

	@Override
	public VersionRange acceptableMinecraftVersionRange() {
		return mod.acceptableMinecraftVersionRange();
	}

	@Override
	public Certificate getSigningCertificate() {
		return mod.getSigningCertificate();
	}

	@Override
	public Map<String, String> getCustomModProperties() {
		return mod.getCustomModProperties();
	}

	@Override
	public Class<?> getCustomResourcePackClass() {
		return mod.getCustomResourcePackClass();
	}

	@Override
	public Map<String, String> getSharedModDescriptor() {
		return mod.getSharedModDescriptor();
	}

	@Override
	public Disableable canBeDisabled() {
		return mod.canBeDisabled();
	}

	@Override
	public String getGuiClassName() {
		return mod.getGuiClassName();
	}

	@Override
	public List<String> getOwnedPackages() {
		return mod.getOwnedPackages();
	}

	@Override
	public boolean shouldLoadInEnvironment() {
		return mod.shouldLoadInEnvironment();
	}

	@Override
	public URL getUpdateUrl() {
		return mod.getUpdateUrl();
	}

}
