package net.pl3x.stairs.block.stairs;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.pl3x.stairs.block.BlockBase;
import net.pl3x.stairs.block.ModBlocks;

import java.util.List;
import java.util.Queue;
import java.util.Random;

public class StairsSponge extends BlockBase {
    private final boolean isWet;

    public StairsSponge(String name, boolean isWet) {
        super(Material.SPONGE, name);
        setSoundType(SoundType.PLANT);
        setHardness(0.6F);

        this.isWet = isWet;
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.YELLOW;
    }

    @Override
    public String getLocalizedName() {
        return I18n.translateToLocal(getUnlocalizedName() + ".name");
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        tryAbsorb(world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        tryAbsorb(world, pos);
        super.neighborChanged(state, world, pos, block, fromPos);
    }

    private void setState(World world, BlockPos pos, StairsSponge type) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, type.getDefaultState().withProperty(FACING, state.getValue(FACING)), 2);
    }

    private void tryAbsorb(World world, BlockPos pos) {
        if (!isWet && absorb(world, pos)) {
            setState(world, pos, ModBlocks.STAIRS_SPONGE_WET);
            world.playEvent(2001, pos, Block.getIdFromBlock(Blocks.WATER));
        }
    }

    private boolean absorb(World world, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
        List<BlockPos> list = Lists.newArrayList();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;
        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos pos1 = tuple.getFirst();
            int j = tuple.getSecond();
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos pos2 = pos1.offset(facing);
                if (world.getBlockState(pos2).getMaterial() == Material.WATER) {
                    world.setBlockState(pos2, Blocks.AIR.getDefaultState(), 2);
                    list.add(pos2);
                    ++i;
                    if (j < 6) {
                        queue.add(new Tuple<>(pos2, j + 1));
                    }
                }
            }
            if (i > 64) {
                break;
            }
        }
        for (BlockPos pos3 : list) {
            world.notifyNeighborsOfStateChange(pos3, Blocks.AIR, false);
        }
        return i > 0;
    }

    @Override
    public void getSubBlocks(CreativeTabs item, NonNullList<ItemStack> items) {
        items.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
        items.add(new ItemStack(Item.getItemFromBlock(this), 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (!isWet) {
            return;
        }
        EnumFacing facing = EnumFacing.random(rand);
        if (facing != EnumFacing.UP && !world.getBlockState(pos.offset(facing)).isTopSolid()) {
            double d0 = (double) pos.getX();
            double d1 = (double) pos.getY();
            double d2 = (double) pos.getZ();
            if (facing == EnumFacing.DOWN) {
                d1 = d1 - 0.05D;
                d0 += rand.nextDouble();
                d2 += rand.nextDouble();
            } else {
                d1 = d1 + rand.nextDouble() * 0.8D;
                if (facing.getAxis() == EnumFacing.Axis.X) {
                    d2 += rand.nextDouble();
                    if (facing == EnumFacing.EAST) {
                        ++d0;
                    } else {
                        d0 += 0.05D;
                    }
                } else {
                    d0 += rand.nextDouble();
                    if (facing == EnumFacing.SOUTH) {
                        ++d2;
                    } else {
                        d2 += 0.05D;
                    }
                }
            }
            world.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
