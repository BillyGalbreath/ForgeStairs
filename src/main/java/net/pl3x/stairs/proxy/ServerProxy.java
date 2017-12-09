package net.pl3x.stairs.proxy;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.pl3x.stairs.block.ModBlocks;
import net.pl3x.stairs.block.stairs.StairsJukebox;

public class ServerProxy {
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerItemRenderer(Item item, int meta, String id) {
    }

    @SubscribeEvent
    public void on(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }
        if (event.getWorld().isRemote) {
            return;
        }
        IBlockState state = event.getWorld().getBlockState(event.getPos());
        if (state.getBlock() != ModBlocks.STAIRS_JUKEBOX) {
            return;
        }
        StairsJukebox jukebox = (StairsJukebox) state.getBlock();
        if (state.getValue(StairsJukebox.HAS_RECORD)) {
            jukebox.dropRecord(event.getWorld(), event.getPos(), state);
            return;
        }
        ItemStack record = event.getItemStack();
        if (!(record.getItem() instanceof ItemRecord)) {
            return;
        }
        jukebox.insertRecord(event.getWorld(), event.getPos(), state, record);
        event.getEntityPlayer().addStat(StatList.RECORD_PLAYED);
    }
}
