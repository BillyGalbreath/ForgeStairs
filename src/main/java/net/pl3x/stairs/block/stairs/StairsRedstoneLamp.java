package net.pl3x.stairs.block.stairs;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pl3x.stairs.block.ModBlocks;

import java.util.Random;

public class StairsRedstoneLamp extends StairsBasic {
    private final boolean isOn;

    public StairsRedstoneLamp(String name, boolean isOn) {
        super(Material.REDSTONE_LIGHT, name, MapColor.AIR);
        setSoundType(SoundType.GLASS);
        setHardness(0.3F);

        if (isOn) {
            setLightLevel(1F);
        }

        this.isOn = isOn;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        if (!world.isRemote) {
            if (isOn && !world.isBlockPowered(pos)) {
                setState(world, pos, ModBlocks.STAIRS_REDSTONE_LAMP);
            } else if (!isOn && world.isBlockPowered(pos)) {
                setState(world, pos, ModBlocks.STAIRS_REDSTONE_LAMP_LIT);
            }
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) {
            if (isOn && !world.isBlockPowered(pos)) {
                world.scheduleUpdate(pos, this, 4);
            } else if (!isOn && world.isBlockPowered(pos)) {
                setState(world, pos, ModBlocks.STAIRS_REDSTONE_LAMP_LIT);
            }
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            if (isOn && !world.isBlockPowered(pos)) {
                setState(world, pos, ModBlocks.STAIRS_REDSTONE_LAMP);
            }
        }
    }

    private void setState(World world, BlockPos pos, StairsRedstoneLamp type) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, type.getDefaultState().withProperty(FACING, state.getValue(FACING)), 2);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.STAIRS_REDSTONE_LAMP);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(ModBlocks.STAIRS_REDSTONE_LAMP), 1);
    }

    @Override
    public ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(ModBlocks.STAIRS_REDSTONE_LAMP), 1);
    }
}
