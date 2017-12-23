package net.pl3x.stairs;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import net.pl3x.stairs.block.ModBlocks;
import net.pl3x.stairs.proxy.ServerProxy;
import net.pl3x.stairs.recipe.DummyRecipe;

@Mod(modid = Stairs.modId, name = Stairs.name, version = Stairs.version)
public class Stairs {
    public static final String modId = "stairs";
    public static final String name = "Stairs";
    public static final String version = "@DEV_BUILD@";

    @SidedProxy(serverSide = "net.pl3x.stairs.proxy.ServerProxy", clientSide = "net.pl3x.stairs.proxy.ClientProxy")
    public static ServerProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ModBlocks.registerItemBlocks(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            ModBlocks.register(event.getRegistry());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            ModBlocks.registerModels();
        }

        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
            // nasty nasty nasty... will need to replace this with whatever lex comes up with that's better (if ever..)
            // http://www.minecraftforge.net/forum/topic/59732-how-best-to-avoid-dangerous-alternative-prefix-when-overriding-registry-entries/
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
            ModContainer mc = Loader.instance().activeModContainer();
            Loader.instance().setActiveModContainer(Loader.instance().getIndexedModList().get("minecraft"));
            modRegistry.register(new DummyRecipe().setRegistryName(modRegistry.remove(new ResourceLocation("minecraft:quartz_stairs")).getRegistryName()));
            modRegistry.register(new DummyRecipe().setRegistryName(modRegistry.remove(new ResourceLocation("minecraft:red_sandstone_stairs")).getRegistryName()));
            modRegistry.register(new DummyRecipe().setRegistryName(modRegistry.remove(new ResourceLocation("minecraft:sandstone_stairs")).getRegistryName()));
            modRegistry.register(new DummyRecipe().setRegistryName(modRegistry.remove(new ResourceLocation("minecraft:stone_brick_stairs")).getRegistryName()));
            Loader.instance().setActiveModContainer(mc);
        }
    }
}
