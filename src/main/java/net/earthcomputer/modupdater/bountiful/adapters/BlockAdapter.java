package net.earthcomputer.modupdater.bountiful.adapters;

import static net.earthcomputer.modupdater.core.MethodPair.EnumType.NEWER;
import static net.earthcomputer.modupdater.core.MethodPair.EnumType.OLDER;

import java.util.List;
import java.util.Random;

import net.earthcomputer.modupdater.bountiful.oldclasses.IIcon;
import net.earthcomputer.modupdater.core.AdapterMethod;
import net.earthcomputer.modupdater.core.MethodPair;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAdapter extends Block {

	// blockConstructorCalled
	protected boolean field_149791_x = true;
	// blockIcon
	@SideOnly(Side.CLIENT)
	protected IIcon field_149761_L;
	// textureName
	protected String field_149768_d;

	public BlockAdapter(Material materialIn) {
		super(materialIn);
	}

	@Override
	@MethodPair(name = "getMapColor", type = NEWER)
	public MapColor getMapColor(IBlockState state) {
		return func_149728_f(getMetaFromState(state));
	}

	@MethodPair(name = "getMapColor", type = OLDER)
	public MapColor func_149728_f(int meta) {
		return getMapColor(getStateFromMeta(meta));
	}

	@Override
	@MethodPair(name = "getBlocksMovement", type = NEWER)
	public boolean isPassable(IBlockAccess world, BlockPos pos) {
		return func_149655_b(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@MethodPair(name = "getBlocksMovement", type = OLDER)
	public boolean func_149655_b(IBlockAccess world, int x, int y, int z) {
		return isPassable(world, new BlockPos(x, y, z));
	}

	@Override
	@MethodPair(name = "getBlockHardness", type = NEWER)
	public float getBlockHardness(World world, BlockPos pos) {
		return func_149712_f(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@MethodPair(name = "getBlockHardness", type = OLDER)
	public float func_149712_f(World world, int x, int y, int z) {
		return getBlockHardness(world, new BlockPos(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@MethodPair(name = "getMixedBrightnessForBlock", type = NEWER)
	public int getMixedBrightnessForBlock(IBlockAccess world, BlockPos pos) {
		return func_149677_c(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@SideOnly(Side.CLIENT)
	@MethodPair(name = "getMixedBrightnessForBlock", type = OLDER)
	public int func_149677_c(IBlockAccess world, int x, int y, int z) {
		return getMixedBrightnessForBlock(world, new BlockPos(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@MethodPair(name = "shouldSideBeRendered", type = NEWER)
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return func_149646_a(world, pos.getX(), pos.getY(), pos.getZ(), side.getIndex());
	}

	@SideOnly(Side.CLIENT)
	@MethodPair(name = "shouldSideBeRendered", type = OLDER)
	public boolean func_149646_a(IBlockAccess world, int x, int y, int z, int side) {
		return shouldSideBeRendered(world, new BlockPos(x, y, z), EnumFacing.getFront(side));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@MethodPair(name = "isBlockSolid", type = NEWER)
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return func_149747_d(world, pos.getX(), pos.getY(), pos.getZ(), side.getIndex());
	}

	@SideOnly(Side.CLIENT)
	@MethodPair(name = "isBlockSolid", type = OLDER)
	public boolean func_149747_d(IBlockAccess world, int x, int y, int z, int side) {
		return isBlockSolid(world, new BlockPos(x, y, z), EnumFacing.getFront(side));
	}

	// getIcon
	@SideOnly(Side.CLIENT)
	@AdapterMethod
	public IIcon func_149673_e(IBlockAccess world, int x, int y, int z, int side) {
		return func_149691_a(side, getMetaFromState(world.getBlockState(new BlockPos(x, y, z))));
	}

	// getIcon
	@SideOnly(Side.CLIENT)
	@AdapterMethod
	public IIcon func_149691_a(int side, int meta) {
		return field_149761_L;
	}

	@Override
	@MethodPair(name = "addCollisionBoxesToList", type = NEWER)
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB bb,
			@SuppressWarnings("rawtypes") List list, Entity entity) {
		func_149743_a(world, pos.getX(), pos.getY(), pos.getZ(), bb, list, entity);
	}

	@MethodPair(name = "addCollisionBoxesToList", type = OLDER)
	public void func_149743_a(World world, int x, int y, int z, AxisAlignedBB bb,
			@SuppressWarnings("rawtypes") List list, Entity entity) {
		BlockPos pos = new BlockPos(x, y, z);
		addCollisionBoxesToList(world, pos, world.getBlockState(pos), bb, list, entity);
	}

	// getBlockTextureFromSide
	@SideOnly(Side.CLIENT)
	@AdapterMethod
	public IIcon func_149733_h(int side) {
		return func_149691_a(side, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@MethodPair(name = "getSelectedBoundingBoxFromPool", type = NEWER)
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return func_149633_g(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@SideOnly(Side.CLIENT)
	@MethodPair(name = "getSelectedBoundingBoxFromPool", type = OLDER)
	public AxisAlignedBB func_149633_g(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		return getCollisionBoundingBox(world, pos, world.getBlockState(pos));
	}

	@Override
	@MethodPair(name = "canCollideCheck", type = NEWER)
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
		return func_149678_a(getMetaFromState(state), hitIfLiquid);
	}

	@MethodPair(name = "canCollideCheck", type = OLDER)
	public boolean func_149678_a(int meta, boolean hitIfLiquid) {
		return canCollideCheck(getStateFromMeta(meta), hitIfLiquid);
	}

	@Override
	@MethodPair(name = "updateTick", type = NEWER)
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		func_149674_a(world, pos.getX(), pos.getY(), pos.getZ(), rand);
	}

	@MethodPair(name = "updateTick", type = OLDER)
	public void func_149674_a(World world, int x, int y, int z, Random rand) {
		BlockPos pos = new BlockPos(x, y, z);
		updateTick(world, pos, world.getBlockState(pos), rand);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@MethodPair(name = "randomDisplayTick", type = NEWER)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
		func_149734_b(world, pos.getX(), pos.getY(), pos.getZ(), rand);
	}

	@SideOnly(Side.CLIENT)
	@MethodPair(name = "randomDisplayTick", type = OLDER)
	public void func_149734_b(World world, int x, int y, int z, Random rand) {
		BlockPos pos = new BlockPos(x, y, z);
		randomDisplayTick(world, pos, world.getBlockState(pos), rand);
	}

	@Override
	@MethodPair(name = "onBlockDestroyedByPlayer", type = NEWER)
	public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
		func_149664_b(world, pos.getX(), pos.getY(), pos.getZ(), getMetaFromState(state));
	}

	@MethodPair(name = "onBlockDestroyedByPlayer", type = OLDER)
	public void func_149664_b(World world, int x, int y, int z, int meta) {
		onBlockDestroyedByPlayer(world, new BlockPos(x, y, z), getStateFromMeta(meta));
	}

	@Override
	@MethodPair(name = "onNeighborBlockChange", type = NEWER)
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		func_149695_a(world, pos.getX(), pos.getY(), pos.getZ(), neighborBlock);
	}

	@MethodPair(name = "onNeighborBlockChange", type = OLDER)
	public void func_149695_a(World world, int x, int y, int z, Block neighborBlock) {
		BlockPos pos = new BlockPos(x, y, z);
		onNeighborBlockChange(world, pos, world.getBlockState(pos), neighborBlock);
	}

	@Override
	@MethodPair(name = "onBlockAdded", type = NEWER)
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		func_149726_b(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@MethodPair(name = "onBlockAdded", type = OLDER)
	public void func_149726_b(World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		onBlockAdded(world, pos, world.getBlockState(pos));
	}

	@Override
	@MethodPair(name = "breakBlock", type = NEWER)
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		func_149749_a(world, pos.getX(), pos.getY(), pos.getZ(), this, getMetaFromState(state));
	}

	@MethodPair(name = "breakBlock", type = OLDER)
	public void func_149749_a(World world, int x, int y, int z, Block block, int meta) {
		breakBlock(world, new BlockPos(x, y, z), getStateFromMeta(meta));
	}

	@Override
	@MethodPair(name = "getItemDropped", type = NEWER)
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return func_149650_a(getMetaFromState(state), rand, fortune);
	}

	@MethodPair(name = "getItemDropped", type = OLDER)
	public Item func_149650_a(int meta, Random rand, int fortune) {
		return getItemDropped(getStateFromMeta(meta), rand, fortune);
	}

	@Override
	@MethodPair(name = "getPlayerRelativeBlockHardness", type = NEWER)
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos) {
		return func_149737_a(player, world, pos.getX(), pos.getY(), pos.getZ());
	}

	@MethodPair(name = "getPlayerRelativeBlockHardness", type = OLDER)
	public float func_149737_a(EntityPlayer player, World world, int x, int y, int z) {
		return getPlayerRelativeBlockHardness(player, world, new BlockPos(x, y, z));
	}

	// dropBlockAsItem
	@AdapterMethod
	public final void func_149642_a(World world, int x, int y, int z, int meta, int fortune) {
		dropBlockAsItem(world, new BlockPos(x, y, z), getStateFromMeta(meta), fortune);
	}

	@Override
	@MethodPair(name = "dropBlockAsItemWithChance", type = NEWER)
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
		func_149690_a(world, pos.getX(), pos.getY(), pos.getZ(), getMetaFromState(state), chance, fortune);
	}

	@MethodPair(name = "dropBlockAsItemWithChance", type = OLDER)
	public void func_149690_a(World world, int x, int y, int z, int meta, float chance, int fortune) {
		dropBlockAsItemWithChance(world, new BlockPos(x, y, z), getStateFromMeta(meta), chance, fortune);
	}
	
	// TODO: dropBlockAsItem, spawnAsEntity issues

}
