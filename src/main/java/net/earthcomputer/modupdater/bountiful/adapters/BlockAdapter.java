package net.earthcomputer.modupdater.bountiful.adapters;

import static net.earthcomputer.modupdater.core.MethodPair.EnumType.NEWER;
import static net.earthcomputer.modupdater.core.MethodPair.EnumType.OLDER;

import java.util.List;
import java.util.Random;

import net.earthcomputer.modupdater.bountiful.oldclasses.IIcon;
import net.earthcomputer.modupdater.core.AccessMethod;
import net.earthcomputer.modupdater.core.AdapterField;
import net.earthcomputer.modupdater.core.AdapterMethod;
import net.earthcomputer.modupdater.core.MethodPair;
import net.earthcomputer.modupdater.core.ModUpdaterPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAdapter extends Block {

	// blockConstructorCalled
	@AdapterField
	protected boolean field_149791_x = true;
	// blockIcon
	@SideOnly(Side.CLIENT)
	@AdapterField
	protected IIcon field_149761_L;
	// textureName
	@AdapterField
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
	public final void func_149697_b(World world, int x, int y, int z, int meta, int fortune) {
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

	@AccessMethod
	@AdapterMethod
	public static void spawnAsEntityPolymorphic(World world, BlockPos pos, ItemStack stack) {
		Block block = stack == null ? null : getBlockFromItem(stack.getItem());
		if (block == null)
			spawnAsEntity(world, pos, stack);
		else
			((BlockAdapter) block).func_149642_a(world, pos.getX(), pos.getY(), pos.getZ(), stack);
	}

	@AccessMethod
	@AdapterMethod
	protected void func_149642_a(World world, int x, int y, int z, ItemStack stack) {
		spawnAsEntity(world, new BlockPos(x, y, z), stack);
	}

	@Override
	@MethodPair(name = "dropXpOnBlockBreak", type = NEWER)
	public void dropXpOnBlockBreak(World world, BlockPos pos, int amt) {
		func_149657_c(world, pos.getX(), pos.getY(), pos.getZ(), amt);
	}

	@MethodPair(name = "dropXpOnBlockBreak", type = OLDER)
	public void func_149657_c(World world, int x, int y, int z, int amt) {
		dropXpOnBlockBreak(world, new BlockPos(x, y, z), amt);
	}

	@Override
	@MethodPair(name = "damageDropped", type = NEWER)
	public int damageDropped(IBlockState state) {
		return func_149692_a(getMetaFromState(state));
	}

	@MethodPair(name = "damageDropped", type = OLDER)
	public int func_149692_a(int meta) {
		return damageDropped(getStateFromMeta(meta));
	}

	@Override
	@MethodPair(name = "collisionRayTrace", type = NEWER)
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		return func_149731_a(world, pos.getX(), pos.getY(), pos.getZ(), start, end);
	}

	@MethodPair(name = "collisionRayTrace", type = OLDER)
	public MovingObjectPosition func_149731_a(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		return collisionRayTrace(world, new BlockPos(x, y, z), start, end);
	}

	@Override
	@MethodPair(name = "onBlockDestroyedByExplosion", type = NEWER)
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		func_149723_a(world, pos.getX(), pos.getY(), pos.getZ(), explosion);
	}

	@MethodPair(name = "onBlockDestroyedByExplosion", type = OLDER)
	public void func_149723_a(World world, int x, int y, int z, Explosion explosion) {
		onBlockDestroyedByExplosion(world, new BlockPos(x, y, z), explosion);
	}

	@Override
	@MethodPair(name = "canReplace", type = NEWER)
	public boolean canReplace(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
		return func_149705_a(world, pos.getX(), pos.getY(), pos.getZ(), side.getIndex(), stack);
	}

	@MethodPair(name = "canReplace", type = OLDER)
	public boolean func_149705_a(World world, int x, int y, int z, int side, ItemStack stack) {
		return canReplace(world, new BlockPos(x, y, z), EnumFacing.getFront(side), stack);
	}

	@Override
	@MethodPair(name = "getRenderBlockPass", type = NEWER)
	public EnumWorldBlockLayer getBlockLayer() {
		return func_149701_w() == 0 ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.TRANSLUCENT;
	}

	@MethodPair(name = "getRenderBlockPass", type = OLDER)
	public int func_149701_w() {
		switch (getBlockLayer()) {
		case CUTOUT:
			return 0;
		case CUTOUT_MIPPED:
			return 0;
		case SOLID:
			return 0;
		case TRANSLUCENT:
			return 1;
		default:
			return -1;
		}
	}

	@Override
	@MethodPair(name = "canPlaceBlockOnSide", type = NEWER)
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return func_149707_d(world, pos.getX(), pos.getY(), pos.getZ(), side.getIndex());
	}

	@MethodPair(name = "canPlaceBlockOnSide", type = OLDER)
	public boolean func_149707_d(World world, int x, int y, int z, int side) {
		return canPlaceBlockOnSide(world, new BlockPos(x, y, z), EnumFacing.getFront(side));
	}

	@Override
	@MethodPair(name = "canPlaceBlockAt", type = NEWER)
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return func_149742_c(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@MethodPair(name = "canPlaceBlockAt", type = OLDER)
	public boolean func_149742_c(World world, int x, int y, int z) {
		return canPlaceBlockAt(world, new BlockPos(x, y, z));
	}

	@Override
	@MethodPair(name = "onBlockActivated", type = NEWER)
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		return func_149727_a(world, pos.getX(), pos.getY(), pos.getZ(), player, side.getIndex(), hitX, hitY, hitZ);
	}

	@MethodPair(name = "onBlockActivated", type = OLDER)
	public boolean func_149727_a(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
			float hitY, float hitZ) {
		BlockPos pos = new BlockPos(x, y, z);
		return onBlockActivated(world, pos, world.getBlockState(pos), player, EnumFacing.getFront(side), hitX, hitY,
				hitZ);
	}

	@Override
	@MethodPair(name = "onEntityWalking", type = NEWER)
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
		func_149724_b(world, pos.getX(), pos.getY(), pos.getZ(), entity);
	}

	@MethodPair(name = "onEntityWalking", type = OLDER)
	public void func_149724_b(World world, int x, int y, int z, Entity entity) {
		onEntityCollidedWithBlock(world, new BlockPos(x, y, z), entity);
	}

	@Override
	@MethodPair(name = "onBlockPlaced", type = NEWER)
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
			int meta, EntityLivingBase placer) {
		return getStateFromMeta(
				func_149660_a(world, pos.getX(), pos.getY(), pos.getZ(), side.getIndex(), hitX, hitY, hitZ, meta));
	}

	@AdapterField
	private boolean hasWarnedOnBlockPlaced = false;

	@MethodPair(name = "onBlockPlaced", type = OLDER)
	public int func_149660_a(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		if (!hasWarnedOnBlockPlaced) {
			ModUpdaterPlugin.LOGGER.warn(
					"The 1.7 version of the method onBlockPlaced has been called. This may cause problems for technical reasons.");
			hasWarnedOnBlockPlaced = true;
		}
		return meta;
	}

	@Override
	@MethodPair(name = "onBlockClicked", type = NEWER)
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		func_149699_a(world, pos.getX(), pos.getY(), pos.getZ(), player);
	}

	@MethodPair(name = "onBlockClicked", type = OLDER)
	public void func_149699_a(World world, int x, int y, int z, EntityPlayer player) {
		onBlockClicked(world, new BlockPos(x, y, z), player);
	}

	@Override
	@MethodPair(name = "velocityToAddToEntity", type = NEWER)
	public Vec3 modifyAcceleration(World world, BlockPos pos, Entity entity, Vec3 vec) {
		func_149640_a(world, pos.getX(), pos.getY(), pos.getZ(), entity, vec);
		return vec;
	}

	@MethodPair(name = "velocityToAddToEntity", type = OLDER)
	public void func_149640_a(World world, int x, int y, int z, Entity entity, Vec3 vec) {
		Vec3 modifiedAcceleration = modifyAcceleration(world, new BlockPos(x, y, z), entity, vec);
		((Vec3Adapter) vec).func_72439_b(modifiedAcceleration.xCoord, modifiedAcceleration.yCoord,
				modifiedAcceleration.zCoord);
	}

	@Override
	@MethodPair(name = "setBlockBoundsBasedOnState", type = NEWER)
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		func_149719_a(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@MethodPair(name = "setBlockBoundsBasedOnState", type = OLDER)
	public void func_149719_a(IBlockAccess world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, new BlockPos(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@MethodPair(name = "getRenderColor", type = NEWER)
	public int getRenderColor(IBlockState state) {
		return func_149741_i(getMetaFromState(state));
	}

	@SideOnly(Side.CLIENT)
	@MethodPair(name = "getRenderColor", type = OLDER)
	public int func_149741_i(int meta) {
		return getRenderColor(getStateFromMeta(meta));
	}

	@Override
	@SideOnly(Side.CLIENT)
	@MethodPair(name = "colorMultiplier", type = NEWER)
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int tint) {
		return func_149720_d(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@SideOnly(Side.CLIENT)
	@MethodPair(name = "colorMultiplier", type = OLDER)
	public int func_149720_d(IBlockAccess world, int x, int y, int z) {
		return colorMultiplier(world, new BlockPos(x, y, z), 0);
	}

	@Override
	@MethodPair(name = "isProvidingWeakPower", type = NEWER)
	public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		return func_149709_b(world, pos.getX(), pos.getY(), pos.getZ(), side.getIndex());
	}

	@MethodPair(name = "isProvidingWeakPower", type = OLDER)
	public int func_149709_b(IBlockAccess world, int x, int y, int z, int side) {
		BlockPos pos = new BlockPos(x, y, z);
		return isProvidingWeakPower(world, pos, world.getBlockState(pos), EnumFacing.getFront(side));
	}

	@Override
	@MethodPair(name = "onEntityCollidedWithBlock", type = NEWER)
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		func_149670_a(world, pos.getX(), pos.getY(), pos.getZ(), entity);
	}

	@MethodPair(name = "onEntityCollidedWithBlock", type = OLDER)
	public void func_149670_a(World world, int x, int y, int z, Entity entity) {
		BlockPos pos = new BlockPos(x, y, z);
		onEntityCollidedWithBlock(world, pos, world.getBlockState(pos), entity);
	}

	@Override
	@MethodPair(name = "isProvidingStrongPower", type = NEWER)
	public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		return func_149748_c(world, pos.getX(), pos.getY(), pos.getZ(), side.getIndex());
	}

	@MethodPair(name = "isProvidingStrongPower", type = OLDER)
	public int func_149748_c(IBlockAccess world, int x, int y, int z, int side) {
		BlockPos pos = new BlockPos(x, y, z);
		return isProvidingStrongPower(world, pos, world.getBlockState(pos), EnumFacing.getFront(side));
	}

	@Override
	@MethodPair(name = "harvestBlock", type = NEWER)
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tileEntity) {
		func_149636_a(world, player, pos.getX(), pos.getY(), pos.getZ(), getMetaFromState(state));
	}

	@MethodPair(name = "harvestBlock", type = OLDER)
	public void func_149636_a(World world, EntityPlayer player, int x, int y, int z, int meta) {
		BlockPos pos = new BlockPos(x, y, z);
		harvestBlock(world, player, pos, getStateFromMeta(meta), world.getTileEntity(pos));
	}

}
