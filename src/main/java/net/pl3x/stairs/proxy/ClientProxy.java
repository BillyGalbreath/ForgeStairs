package net.pl3x.stairs.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.pl3x.stairs.Stairs;
import net.pl3x.stairs.block.ModBlocks;
import net.pl3x.stairs.block.stairs.StairsTNT;
import net.pl3x.stairs.color.ModColorManager;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelLoader.setCustomStateMapper(ModBlocks.STAIRS_TNT,
                new StateMap.Builder().ignore(StairsTNT.EXPLODE).build());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        ModColorManager.registerColorHandlers();
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        super.registerItemRenderer(item, meta, id);
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(Stairs.modId + ":" + id, "inventory"));
    }
}
