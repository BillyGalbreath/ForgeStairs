package net.pl3x.stairs.block.stairs;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.pl3x.stairs.block.ModBlocks;

import java.util.Random;

public class BlockStairsRedstoneOre extends BlockStairsBasic {
    private final boolean isOn;

    public BlockStairsRedstoneOre(String name, boolean isOn) {
        super(Material.ROCK, name, MapColor.STONE);
        setSoundType(SoundType.STONE);
        setHardness(3F);
        setResistance(5F);

        if (isOn) {
            setLightLevel(0.625F);
            setTickRandomly(true);
        }

        this.isOn = isOn;
    }

    @Override
    public int tickRate(World world) {
        return 30;
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        activate(world, pos);
        super.onBlockClicked(world, pos, player);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        activate(world, pos);
        super.onEntityWalk(world, pos, entity);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        activate(world, pos);
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    private void activate(World world, BlockPos pos) {
        spawnParticles(world, pos);

        if (this == ModBlocks.STAIRS_REDSTONE_ORE) {
            setState(world, pos, ModBlocks.STAIRS_REDSTONE_ORE_LIT);
        }
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (this == ModBlocks.STAIRS_REDSTONE_ORE_LIT) {
            setState(world, pos, ModBlocks.STAIRS_REDSTONE_ORE);
        }
    }

    private void setState(World world, BlockPos pos, BlockStairsRedstoneOre type) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, type.getDefaultState().withProperty(FACING, state.getValue(FACING)));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (isOn) {
            spawnParticles(world, pos);
        }
    }

    private void spawnParticles(World world, BlockPos pos) {
        Random random = world.rand;
        for (int i = 0; i < 6; ++i) {
            double d1 = (double) ((float) pos.getX() + random.nextFloat());
            double d2 = (double) ((float) pos.getY() + random.nextFloat());
            double d3 = (double) ((float) pos.getZ() + random.nextFloat());
            if (i == 0 && !world.getBlockState(pos.up()).isOpaqueCube()) {
                d2 = (double) pos.getY() + 0.0625D + 1.0D;
            }
            if (i == 1 && !world.getBlockState(pos.down()).isOpaqueCube()) {
                d2 = (double) pos.getY() - 0.0625D;
            }
            if (i == 2 && !world.getBlockState(pos.south()).isOpaqueCube()) {
                d3 = (double) pos.getZ() + 0.0625D + 1.0D;
            }
            if (i == 3 && !world.getBlockState(pos.north()).isOpaqueCube()) {
                d3 = (double) pos.getZ() - 0.0625D;
            }
            if (i == 4 && !world.getBlockState(pos.east()).isOpaqueCube()) {
                d1 = (double) pos.getX() + 0.0625D + 1.0D;
            }
            if (i == 5 && !world.getBlockState(pos.west()).isOpaqueCube()) {
                d1 = (double) pos.getX() - 0.0625D;
            }
            if (d1 < (double) pos.getX() || d1 > (double) (pos.getX() + 1) || d2 < 0.0D || d2 > (double) (pos.getY() + 1) || d3 < (double) pos.getZ() || d3 > (double) (pos.getZ() + 1)) {
                world.spawnParticle(EnumParticleTypes.REDSTONE, d1, d2, d3, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.STAIRS_REDSTONE_ORE);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(ModBlocks.STAIRS_REDSTONE_ORE), 1);
    }

    @Override
    public ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(ModBlocks.STAIRS_REDSTONE_ORE), 1);
    }
}
