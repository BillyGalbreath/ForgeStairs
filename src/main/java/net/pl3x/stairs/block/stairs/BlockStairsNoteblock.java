package net.pl3x.stairs.block.stairs;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.pl3x.stairs.block.BlockBase;
import net.pl3x.stairs.block.ModBlocks;

import java.util.List;

public class BlockStairsNoteblock extends BlockBase implements ITileEntityProvider {
    private static final List<SoundEvent> INSTRUMENTS = Lists.newArrayList(SoundEvents.BLOCK_NOTE_HARP, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundEvents.BLOCK_NOTE_SNARE, SoundEvents.BLOCK_NOTE_HAT, SoundEvents.BLOCK_NOTE_BASS, SoundEvents.BLOCK_NOTE_FLUTE, SoundEvents.BLOCK_NOTE_BELL, SoundEvents.BLOCK_NOTE_GUITAR, SoundEvents.BLOCK_NOTE_CHIME, SoundEvents.BLOCK_NOTE_XYLOPHONE);

    public BlockStairsNoteblock() {
        super(Material.WOOD, "stairs_noteblock");
        setSoundType(SoundType.WOOD);
        setHardness(0.8F);
        hasTileEntity = true;
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.WOOD;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        boolean flag = world.isBlockPowered(pos);
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof TileEntityNote) {
            TileEntityNote tileentitynote = (TileEntityNote) tileentity;
            if (tileentitynote.previousRedstoneState != flag) {
                if (flag) {
                    tileentitynote.triggerNote(world, pos);
                }
                tileentitynote.previousRedstoneState = flag;
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityNote) {
                TileEntityNote tileentitynote = (TileEntityNote) tileentity;
                int old = tileentitynote.note;
                tileentitynote.changePitch();
                if (old == tileentitynote.note) {
                    return false;
                }
                tileentitynote.triggerNote(world, pos);
                player.addStat(StatList.NOTEBLOCK_TUNED);
            }
        }
        return true;
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        if (!world.isRemote) {
            TileEntity tileentity = world.getTileEntity(pos);
            if (tileentity instanceof TileEntityNote) {
                ((TileEntityNote) tileentity).triggerNote(world, pos);
                player.addStat(StatList.NOTEBLOCK_PLAYED);
            }
        }
    }

    private SoundEvent getInstrument(int eventId) {
        if (eventId < 0 || eventId >= INSTRUMENTS.size()) {
            eventId = 0;
        }
        return INSTRUMENTS.get(eventId);
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        NoteBlockEvent.Play event = new NoteBlockEvent.Play(world, pos, state, param, id);
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        id = event.getInstrument().ordinal();
        param = event.getVanillaNoteId();
        world.playSound(null, pos, getInstrument(id), SoundCategory.RECORDS, 3.0F, (float) Math.pow(2.0D, (double) (param - 12) / 12.0D));
        world.spawnParticle(EnumParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.2D, (double) pos.getZ() + 0.5D, (double) param / 24.0D, 0.0D, 0.0D);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityNote();
    }

    public static class TileEntityNote extends net.minecraft.tileentity.TileEntityNote {
        @Override
        public void triggerNote(World world, BlockPos pos) {
            if (world.getBlockState(pos.up()).getMaterial() == Material.AIR) {
                IBlockState state = world.getBlockState(pos.down());
                Material material = state.getMaterial();
                int i = 0;
                if (material == Material.ROCK) {
                    i = 1;
                }
                if (material == Material.SAND) {
                    i = 2;
                }
                if (material == Material.GLASS) {
                    i = 3;
                }
                if (material == Material.WOOD) {
                    i = 4;
                }
                Block block = state.getBlock();
                if (block == Blocks.CLAY) {
                    i = 5;
                }
                if (block == Blocks.GOLD_BLOCK) {
                    i = 6;
                }
                if (block == Blocks.WOOL) {
                    i = 7;
                }
                if (block == Blocks.PACKED_ICE) {
                    i = 8;
                }
                if (block == Blocks.BONE_BLOCK) {
                    i = 9;
                }
                world.addBlockEvent(pos, ModBlocks.STAIRS_NOTEBLOCK, i, note);
            }
        }

        @Override
        public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
            return oldState.getBlock() != newState.getBlock();
        }
    }
}
