package net.pl3x.stairs.block.stairs;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.pl3x.stairs.block.BlockBase;

import java.util.Random;

public class StairsIce extends BlockBase {
    public StairsIce() {
        super(Material.ICE, "stairs_ice");
        setSoundType(SoundType.GLASS);
        setHardness(0.5F);
        setLightOpacity(3);
        setDefaultSlipperiness(0.98F);
        setTickRandomly(true);
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.ICE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (world.getLightFor(EnumSkyBlock.BLOCK, pos) > 11 - getDefaultState().getLightOpacity()) {
            turnIntoWater(world, pos);
        }
    }

    public void turnIntoWater(World world, BlockPos pos) {
        if (world.provider.doesWaterVaporize()) {
            world.setBlockToAir(pos);
        } else {
            dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
            world.setBlockState(pos, Blocks.WATER.getDefaultState());
            world.neighborChanged(pos, Blocks.WATER, pos);
        }
    }

    @Override
    public EnumPushReaction getMobilityFlag(IBlockState state) {
        return EnumPushReaction.NORMAL;
    }
}
