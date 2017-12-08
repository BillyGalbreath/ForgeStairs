package net.pl3x.stairs.block.stairs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
                    EntityFallingStairs entity = new EntityFallingStairs(world, (double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, world.getBlockState(pos));
                    onStartFalling(entity);
                    world.spawnEntity(entity);
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

    public void onStartFalling(EntityFallingStairs fallingEntity) {
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

    public static class EntityFallingStairs extends EntityFallingBlock {
        private IBlockState fallTile;

        public EntityFallingStairs(World world) {
            super(world);
        }

        public EntityFallingStairs(World world, double x, double y, double z, IBlockState state) {
            super(world, x, y, z, state);
            this.fallTile = state;
        }

        public void onUpdate() {
            Block block = fallTile.getBlock();

            if (fallTile.getMaterial() == Material.AIR) {
                setDead();
                return;
            }

            prevPosX = this.posX;
            prevPosY = this.posY;
            prevPosZ = this.posZ;

            if (fallTime++ == 0) {
                BlockPos pos = new BlockPos(this);
                if (world.getBlockState(pos).getBlock() == block) {
                    world.setBlockToAir(pos);
                } else if (!world.isRemote) {
                    setDead();
                    return;
                }
            }

            if (!hasNoGravity()) {
                motionY -= 0.03999999910593033D;
            }

            move(MoverType.SELF, motionX, motionY, motionZ);

            if (!world.isRemote) {
                BlockPos pos1 = new BlockPos(this);
                boolean isPowderStairs = fallTile.getBlock() instanceof BlockStairsConcretePowder;
                boolean powderTouchingWater = isPowderStairs && world.getBlockState(pos1).getMaterial() == Material.WATER;
                double d0 = motionX * motionX + motionY * motionY + motionZ * motionZ;

                if (isPowderStairs && d0 > 1.0D) {
                    RayTraceResult result = world.rayTraceBlocks(new Vec3d(prevPosX, prevPosY, prevPosZ), new Vec3d(posX, posY, posZ), true);
                    if (result != null && world.getBlockState(result.getBlockPos()).getMaterial() == Material.WATER) {
                        pos1 = result.getBlockPos();
                        powderTouchingWater = true;
                    }
                }

                if (!onGround && !powderTouchingWater) {
                    if (fallTime > 100 && !world.isRemote && (pos1.getY() < 1 || pos1.getY() > 256) || fallTime > 600) {
                        if (shouldDropItem && world.getGameRules().getBoolean("doEntityDrops")) {
                            entityDropItem(new ItemStack(block, 1, block.damageDropped(fallTile)), 0.0F);
                        }
                        setDead();
                    }
                } else {
                    IBlockState state1 = world.getBlockState(pos1);
                    if (world.isAirBlock(new BlockPos(posX, posY - 0.009999999776482582D, posZ))) {
                        if (!powderTouchingWater && BlockFalling.canFallThrough(world.getBlockState(new BlockPos(posX, posY - 0.009999999776482582D, posZ)))) {
                            onGround = false;
                            return;
                        }
                    }

                    motionX *= 0.699999988079071D;
                    motionZ *= 0.699999988079071D;
                    motionY *= -0.5D;

                    if (state1.getBlock() != Blocks.PISTON_EXTENSION) {
                        setDead();

                        if (world.mayPlace(block, pos1, true, EnumFacing.UP, null) &&
                                (powderTouchingWater || !BlockFalling.canFallThrough(world.getBlockState(pos1.down()))) &&
                                world.setBlockState(pos1, fallTile, 3)) {
                            if (block instanceof BlockFalling) {
                                ((BlockFalling) block).onEndFalling(world, pos1, fallTile, state1);
                            }

                            if (tileEntityData != null && block.hasTileEntity(fallTile)) {
                                TileEntity tileentity = world.getTileEntity(pos1);
                                if (tileentity != null) {
                                    NBTTagCompound nbttagcompound = tileentity.writeToNBT(new NBTTagCompound());
                                    for (String s : tileEntityData.getKeySet()) {
                                        NBTBase nbtbase = tileEntityData.getTag(s);
                                        if (!"x".equals(s) && !"y".equals(s) && !"z".equals(s)) {
                                            nbttagcompound.setTag(s, nbtbase.copy());
                                        }
                                    }
                                    tileentity.readFromNBT(nbttagcompound);
                                    tileentity.markDirty();
                                }
                            }
                        } else if (shouldDropItem && world.getGameRules().getBoolean("doEntityDrops")) {
                            entityDropItem(new ItemStack(block, 1, block.damageDropped(fallTile)), 0.0F);
                        }
                    }
                }
            }

            motionX *= 0.9800000190734863D;
            motionY *= 0.9800000190734863D;
            motionZ *= 0.9800000190734863D;
        }
    }
}
