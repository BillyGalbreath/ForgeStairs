package net.pl3x.stairs.block.stairs;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pl3x.stairs.block.ModBlocks;

import java.util.Random;

public class StairsConcretePowder extends StairsFalling {
    private final EnumDyeColor color;

    public StairsConcretePowder(EnumDyeColor color) {
        super(Material.SAND, "stairs_concrete_powder_" + color.getName(), MapColor.getBlockColor(color));
        setSoundType(SoundType.SAND);
        setHardness(0.5F);
        this.color = color;
    }

    @Override
    public Item createItemBlock() {
        return new ItemBlock(getBlock(color)).setRegistryName(getRegistryName());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(getBlock(color));
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(getBlock(color));
    }

    @Override
    public void onEndFalling(World world, BlockPos pos, IBlockState fallTile, IBlockState state) {
        if (state.getMaterial().isLiquid()) {
            world.setBlockState(pos, StairsConcrete.getBlock(color).getDefaultState()
                    .withProperty(FACING, fallTile.getValue(FACING))
                    .withProperty(HALF, state.getValue(HALF)), 3);
        }
    }

    public boolean tryTouchWater(World world, BlockPos pos, IBlockState state) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing != EnumFacing.DOWN) {
                BlockPos pos1 = pos.offset(facing);
                if (world.getBlockState(pos1).getMaterial() == Material.WATER) {
                    world.setBlockState(pos, StairsConcrete.getBlock(color).getDefaultState()
                            .withProperty(FACING, state.getValue(FACING))
                            .withProperty(HALF, state.getValue(HALF)), 3);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!tryTouchWater(world, pos, state)) {
            super.neighborChanged(state, world, pos, block, fromPos);
        }
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!tryTouchWater(world, pos, state)) {
            super.onBlockAdded(world, pos, state);
        }
    }

    public static StairsConcretePowder getBlock(EnumDyeColor color) {
        switch (color) {
            case BLACK:
                return ModBlocks.STAIRS_CONCRETE_POWDER_BLACK;
            case BLUE:
                return ModBlocks.STAIRS_CONCRETE_POWDER_BLUE;
            case BROWN:
                return ModBlocks.STAIRS_CONCRETE_POWDER_BROWN;
            case CYAN:
                return ModBlocks.STAIRS_CONCRETE_POWDER_CYAN;
            case GRAY:
                return ModBlocks.STAIRS_CONCRETE_POWDER_GRAY;
            case GREEN:
                return ModBlocks.STAIRS_CONCRETE_POWDER_GREEN;
            case LIGHT_BLUE:
                return ModBlocks.STAIRS_CONCRETE_POWDER_LIGHT_BLUE;
            case LIME:
                return ModBlocks.STAIRS_CONCRETE_POWDER_LIME;
            case MAGENTA:
                return ModBlocks.STAIRS_CONCRETE_POWDER_MAGENTA;
            case ORANGE:
                return ModBlocks.STAIRS_CONCRETE_POWDER_ORANGE;
            case PINK:
                return ModBlocks.STAIRS_CONCRETE_POWDER_PINK;
            case PURPLE:
                return ModBlocks.STAIRS_CONCRETE_POWDER_PURPLE;
            case RED:
                return ModBlocks.STAIRS_CONCRETE_POWDER_RED;
            case SILVER:
                return ModBlocks.STAIRS_CONCRETE_POWDER_SILVER;
            case YELLOW:
                return ModBlocks.STAIRS_CONCRETE_POWDER_YELLOW;
            case WHITE:
            default:
                return ModBlocks.STAIRS_CONCRETE_POWDER_WHITE;
        }
    }
}
