package net.pl3x.stairs.block.stairs;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockStairsFalling extends BlockStairsBasic {
    public static boolean fallInstantly;
    private int dustColor = -16777216;

    public BlockStairsFalling(Material material, String name, MapColor mapColor) {
        super(material, name, mapColor);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.scheduleUpdate(pos, this, tickRate(world));
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        world.scheduleUpdate(pos, this, tickRate(world));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) {
            checkFallable(world, pos);
        }
    }

    private void checkFallable(World world, BlockPos pos) {
        if ((world.isAirBlock(pos.down()) || canFallThrough(world.getBlockState(pos.down()))) && pos.getY() >= 0) {
            if (!fallInstantly && world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
                if (!world.isRemote) {
                    EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, world.getBlockState(pos));
                    onStartFalling(entityfallingblock);
                    world.spawnEntity(entityfallingblock);
                }
            } else {
                IBlockState state = world.getBlockState(pos);
                world.setBlockToAir(pos);
                BlockPos blockpos;
                for (blockpos = pos.down(); (world.isAirBlock(blockpos) || canFallThrough(world.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos = blockpos.down()) {
                    ;
                }
                if (blockpos.getY() > 0) {
                    world.setBlockState(blockpos.up(), state); //Forge: Fix loss of state information during world gen.
                }
            }
        }
    }

    public void onStartFalling(EntityFallingBlock fallingEntity) {
    }

    @Override
    public int tickRate(World world) {
        return 2;
    }

    public static boolean canFallThrough(IBlockState state) {
        Material material = state.getMaterial();
        return state.getBlock() == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
    }

    public void onEndFalling(World world, BlockPos pos, IBlockState fallTile, IBlockState state) {
    }

    public void onBroken(World world, BlockPos pos) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (rand.nextInt(16) == 0) {
            BlockPos blockpos = pos.down();
            if (canFallThrough(world.getBlockState(blockpos))) {
                double d0 = (double) ((float) pos.getX() + rand.nextFloat());
                double d1 = (double) pos.getY() - 0.05D;
                double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
                world.spawnParticle(EnumParticleTypes.FALLING_DUST, d0, d1, d2, 0.0D, 0.0D, 0.0D, Block.getStateId(state));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public int getDustColor(IBlockState state) {
        return dustColor;
    }

    @Override
    public BlockStairsFalling setSoundType(SoundType sound) {
        super.setSoundType(sound);
        return this;
    }

    @Override
    public BlockStairsFalling setResistance(float resistance) {
        super.setResistance(resistance);
        return this;
    }

    @Override
    public BlockStairsFalling setHardness(float hardness) {
        super.setHardness(hardness);
        return this;
    }

    public BlockStairsFalling setDustColor(int dustColor) {
        this.dustColor = dustColor;
        return this;
    }
}
