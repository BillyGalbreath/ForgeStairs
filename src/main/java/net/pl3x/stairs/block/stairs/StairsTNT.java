package net.pl3x.stairs.block.stairs;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.pl3x.stairs.block.BlockBase;

public class StairsTNT extends BlockBase {
    public static final PropertyBool EXPLODE = PropertyBool.create("explode");

    public StairsTNT() {
        super(Material.TNT, "stairs_tnt");
        setSoundType(SoundType.PLANT);
        setHardness(0.0F);

        setDefaultState(blockState.getBaseState().withProperty(EXPLODE, false));
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.TNT;
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (world.isBlockPowered(pos)) {
            onBlockDestroyedByPlayer(world, pos, state.withProperty(EXPLODE, true));
            world.setBlockToAir(pos);
        }
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (world.isBlockPowered(pos)) {
            onBlockDestroyedByPlayer(world, pos, state.withProperty(EXPLODE, true));
            world.setBlockToAir(pos);
        }
    }

    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            EntityTNTPrimed entity = new EntityTNTPrimed(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, explosion.getExplosivePlacedBy());
            entity.setFuse(world.rand.nextInt(entity.getFuse() / 4) + entity.getFuse() / 8);
            world.spawnEntity(entity);
        }
    }

    public void onBlockDestroyedByPlayer(World world, BlockPos pos, IBlockState state) {
        this.explode(world, pos, state, null);
    }

    public void explode(World world, BlockPos pos, IBlockState state, EntityLivingBase igniter) {
        if (!world.isRemote) {
            if (state.getValue(EXPLODE)) {
                EntityTNTPrimed entity = new EntityTNTPrimed(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, igniter);
                world.spawnEntity(entity);
                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty() && (itemstack.getItem() == Items.FLINT_AND_STEEL || itemstack.getItem() == Items.FIRE_CHARGE)) {
            explode(world, pos, state.withProperty(EXPLODE, true), player);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
                itemstack.damageItem(1, player);
            } else if (!player.capabilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            return true;
        } else {
            return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
        }
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        if (!world.isRemote && entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;
            if (entityarrow.isBurning()) {
                explode(world, pos, world.getBlockState(pos).withProperty(EXPLODE, true), entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.shootingEntity : null);
                world.setBlockToAir(pos);
            }
        }
    }

    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }

    /*
    DEC BIN  FACING HALF   EXPLODE
    0   0000 south  bottom no
    1   0001 south  bottom yes
    2   0010 south  top    no
    3   0011 south  top    yes
    4   0100 west   bottom no
    5   0101 west   bottom yes
    6   0110 west   top    no
    7   0111 west   top    yes
    8   1000 north  bottom no
    9   1001 north  bottom yes
    10  1010 north  top    no
    11  1011 north  top    yes
    12  1100 east   bottom no
    13  1101 east   bottom yes
    14  1110 east   top    no
    15  1111 east   top    yes
        ││││
        │││└─── half        (bottom[0], top[1])
        │└└──── facing      (south[00], west[01], north[10], east[11])
        └────── explode     (false[0], true[1])
    */

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(EXPLODE, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = super.getMetaFromState(state);
        if (state.getValue(EXPLODE)) {
            i |= 8;
        }
        return i;
    }

    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HALF, SHAPE, EXPLODE);
    }
}
