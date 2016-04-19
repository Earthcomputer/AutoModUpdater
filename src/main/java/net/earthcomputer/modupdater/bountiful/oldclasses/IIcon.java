package net.earthcomputer.modupdater.bountiful.oldclasses;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IIcon {

	// getIconName
	@SideOnly(Side.CLIENT)
	String func_94215_i();

	// getMinU
	@SideOnly(Side.CLIENT)
	float func_94209_e();

	// getMinV
	@SideOnly(Side.CLIENT)
	float func_94206_g();

	// getMaxU
	@SideOnly(Side.CLIENT)
	float func_94212_f();

	// getMaxV
	@SideOnly(Side.CLIENT)
	float func_94210_h();

	// getInterpolatedU
	@SideOnly(Side.CLIENT)
	float func_94214_a(double d);

	// getInterpolatedV
	@SideOnly(Side.CLIENT)
	float func_94207_b(double d);

	// getIconWidth
	@SideOnly(Side.CLIENT)
	int func_94211_a();

	// getIconHeight
	@SideOnly(Side.CLIENT)
	int func_94216_b();

}
