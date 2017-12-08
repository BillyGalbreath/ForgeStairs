package net.pl3x.stairs.block.stairs;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.pl3x.stairs.block.BlockBase;

public class BlockStairsJukebox extends BlockBase implements ITileEntityProvider {
    public static final PropertyBool HAS_RECORD = PropertyBool.create("has_record");

    public BlockStairsJukebox() {
        super(Material.WOOD, "stairs_jukebox");
        setSoundType(SoundType.STONE);
        setHardness(2F);
        setResistance(10F);
        hasTileEntity = true;

        setDefaultState(getDefaultState().withProperty(HAS_RECORD, false));
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.DIRT;
    }

    public void insertRecord(World world, BlockPos pos, IBlockState state, ItemStack record) {
        if (world.isRemote) {
            return;
        }
        TileEntity tileentity = world.getTileEntity(pos);
        if (!(tileentity instanceof TileEntityJukebox)) {
            return;
        }
        TileEntityJukebox jukebox = (TileEntityJukebox) tileentity;
        jukebox.setRecord(record.copy());
        world.setBlockState(pos, state.withProperty(HAS_RECORD, true), 3);
        world.playEvent(null, 1010, pos, Item.getIdFromItem(record.getItem()));
        record.shrink(1);
    }

    public void dropRecord(World world, BlockPos pos, IBlockState state) {
        if (world.isRemote) {
            return;
        }
        TileEntity tileentity = world.getTileEntity(pos);
        if (!(tileentity instanceof TileEntityJukebox)) {
            return;
        }
        TileEntityJukebox jukebox = (TileEntityJukebox) tileentity;
        ItemStack record = jukebox.getRecord();
        if (record.isEmpty()) {
            return;
        }
        world.playEvent(1010, pos, 0);
        world.playRecord(pos, null);
        jukebox.setRecord(ItemStack.EMPTY);
        world.setBlockState(pos, state.withProperty(HAS_RECORD, false), 3);
        double d0 = (double) (world.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
        double d1 = (double) (world.rand.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
        double d2 = (double) (world.rand.nextFloat() * 0.7F) + 0.15000000596046448D;
        EntityItem entityitem = new EntityItem(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, record.copy());
        entityitem.setDefaultPickupDelay();
        world.spawnEntity(entityitem);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        dropRecord(world, pos, state);
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof TileEntityJukebox) {
            ItemStack record = ((TileEntityJukebox) tileentity).getRecord();
            if (!record.isEmpty()) {
                return Item.getIdFromItem(record.getItem()) + 1 - Item.getIdFromItem(Items.RECORD_13);
            }
        }
        return 0;
    }

    /*
    DEC BIN  FACING HALF   HAS_RECORD
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
        └────── has_record  (false[0], true[1])
    */

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(HAS_RECORD, (meta & 8) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = super.getMetaFromState(state);
        if (state.getValue(HAS_RECORD)) {
            i |= 8;
        }
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HALF, SHAPE, HAS_RECORD);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityJukebox();
    }

    public static class TileEntityJukebox extends net.minecraft.block.BlockJukebox.TileEntityJukebox {
        @Override
        public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
            return oldState.getBlock() != newState.getBlock();
        }
    }
}
