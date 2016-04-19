package net.earthcomputer.modupdater.bountiful.adapters;

import static net.earthcomputer.modupdater.core.MethodPair.EnumType.NEWER;
import static net.earthcomputer.modupdater.core.MethodPair.EnumType.OLDER;

import net.earthcomputer.modupdater.core.MethodPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockAdapter2 extends Block {

	public BlockAdapter2(Material materialIn) {
		super(materialIn);
	}

	@Override
	@MethodPair(name = "getCollisionBoundingBoxFromPool", type = NEWER)
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return func_149668_a(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@MethodPair(name = "getCollisionBoundingBoxFromPool", type = OLDER)
	public AxisAlignedBB func_149668_a(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		return getCollisionBoundingBox(world, pos, world.getBlockState(pos));
	}

}
