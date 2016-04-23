package net.earthcomputer.modupdater.bountiful.adapters;

import net.earthcomputer.modupdater.core.AdapterField;
import net.earthcomputer.modupdater.core.AdapterMethod;
import net.earthcomputer.modupdater.core.Getter;
import net.earthcomputer.modupdater.core.Setter;
import net.minecraft.util.Vec3;

public class Vec3Adapter extends Vec3 {

	@AdapterField
	private double nonFinalXCoord;
	@AdapterField
	private double nonFinalYCoord;
	@AdapterField
	private double nonFinalZCoord;

	public Vec3Adapter(double x, double y, double z) {
		super(x, y, z);
	}

	@Getter
	public final double getNonFinalXCoord() {
		return nonFinalXCoord;
	}

	@Getter
	public final double getNonFinalYCoord() {
		return nonFinalYCoord;
	}

	@Getter
	public final double getNonFinalZCoord() {
		return nonFinalZCoord;
	}

	@Setter
	public final void setNonFinalXCoord(double xCoord) {
		this.nonFinalXCoord = xCoord;
	}

	@Setter
	public final void setNonFinalYCoord(double yCoord) {
		this.nonFinalYCoord = yCoord;
	}

	@Setter
	public final void setNonFinalZCoord(double zCoord) {
		this.nonFinalZCoord = zCoord;
	}

	// setComponents
	@AdapterMethod
	public Vec3 func_72439_b(double x, double y, double z) {
		setNonFinalXCoord(x);
		setNonFinalYCoord(y);
		setNonFinalZCoord(z);
		return this;
	}

}
