package net.pl3x.stairs.color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.pl3x.stairs.block.ModBlocks;

@SideOnly(Side.CLIENT)
public class ModColorManager {
    private static final Minecraft minecraft = Minecraft.getMinecraft();

    public static void registerColorHandlers() {
        final BlockColors blockColors = minecraft.getBlockColors();
        final ItemColors itemColors = minecraft.getItemColors();

        registerBlockColorHandlers(blockColors);
        registerItemColorHandlers(blockColors, itemColors);
    }

    private static void registerBlockColorHandlers(final BlockColors blockColors) {
        final IBlockColor foliageColorHandler = (state, world, pos, tintIndex) -> {
            Block block = state.getBlock();
            if (block == ModBlocks.STAIRS_LEAVES_BIRCH) {
                return ColorizerFoliage.getFoliageColorBirch();
            } else if (block == ModBlocks.STAIRS_LEAVES_SPRUCE) {
                return ColorizerFoliage.getFoliageColorPine();
            } else if (world != null && pos != null) {
                return BiomeColorHelper.getFoliageColorAtPos(world, pos);
            }
            return ColorizerFoliage.getFoliageColorBasic();
        };
        final IBlockColor grassColorHandler = (state, blockAccess, pos, tintIndex) -> {
            if (blockAccess != null && pos != null) {
                return BiomeColorHelper.getGrassColorAtPos(blockAccess, pos);
            }
            return ColorizerGrass.getGrassColor(0.5D, 1.0D);
        };

        blockColors.registerBlockColorHandler(grassColorHandler, ModBlocks.STAIRS_GRASS);
        blockColors.registerBlockColorHandler(foliageColorHandler, ModBlocks.STAIRS_LEAVES_ACACIA);
        blockColors.registerBlockColorHandler(foliageColorHandler, ModBlocks.STAIRS_LEAVES_BIRCH);
        blockColors.registerBlockColorHandler(foliageColorHandler, ModBlocks.STAIRS_LEAVES_DARK_OAK);
        blockColors.registerBlockColorHandler(foliageColorHandler, ModBlocks.STAIRS_LEAVES_JUNGLE);
        blockColors.registerBlockColorHandler(foliageColorHandler, ModBlocks.STAIRS_LEAVES_OAK);
        blockColors.registerBlockColorHandler(foliageColorHandler, ModBlocks.STAIRS_LEAVES_SPRUCE);
    }

    private static void registerItemColorHandlers(final BlockColors blockColors, final ItemColors itemColors) {
        final IItemColor itemBlockColorHandler = (stack, tintIndex) ->
                blockColors.colorMultiplier(((ItemBlock) stack.getItem()).getBlock()
                        .getStateFromMeta(stack.getMetadata()), null, null, tintIndex);

        itemColors.registerItemColorHandler(itemBlockColorHandler, ModBlocks.STAIRS_GRASS);
        itemColors.registerItemColorHandler(itemBlockColorHandler, ModBlocks.STAIRS_LEAVES_ACACIA);
        itemColors.registerItemColorHandler(itemBlockColorHandler, ModBlocks.STAIRS_LEAVES_BIRCH);
        itemColors.registerItemColorHandler(itemBlockColorHandler, ModBlocks.STAIRS_LEAVES_DARK_OAK);
        itemColors.registerItemColorHandler(itemBlockColorHandler, ModBlocks.STAIRS_LEAVES_JUNGLE);
        itemColors.registerItemColorHandler(itemBlockColorHandler, ModBlocks.STAIRS_LEAVES_OAK);
        itemColors.registerItemColorHandler(itemBlockColorHandler, ModBlocks.STAIRS_LEAVES_SPRUCE);
    }
}
