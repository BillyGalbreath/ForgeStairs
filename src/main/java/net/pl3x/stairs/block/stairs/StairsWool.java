package net.pl3x.stairs.block.stairs;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.pl3x.stairs.block.BlockBase;
import net.pl3x.stairs.block.ModBlocks;

import java.util.Random;

public class StairsWool extends BlockBase {
    private final EnumDyeColor color;

    public StairsWool(EnumDyeColor color) {
        super(Material.CLOTH, "stairs_wool_" + color.getName());
        setSoundType(SoundType.CLOTH);
        setHardness(0.8F);
        this.color = color;
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.getBlockColor(color);
    }

    @Override
    public Item createItemBlock() {
        return new ItemBlock(getBlock(color)).setRegistryName(getRegistryName());
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(getBlock(color));
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos, IBlockState state) {
        return new ItemStack(getBlock(color));
    }

    public static StairsWool getBlock(EnumDyeColor color) {
        switch (color) {
            case BLACK:
                return ModBlocks.STAIRS_WOOL_BLACK;
            case BLUE:
                return ModBlocks.STAIRS_WOOL_BLUE;
            case BROWN:
                return ModBlocks.STAIRS_WOOL_BROWN;
            case CYAN:
                return ModBlocks.STAIRS_WOOL_CYAN;
            case GRAY:
                return ModBlocks.STAIRS_WOOL_GRAY;
            case GREEN:
                return ModBlocks.STAIRS_WOOL_GREEN;
            case LIGHT_BLUE:
                return ModBlocks.STAIRS_WOOL_LIGHT_BLUE;
            case LIME:
                return ModBlocks.STAIRS_WOOL_LIME;
            case MAGENTA:
                return ModBlocks.STAIRS_WOOL_MAGENTA;
            case ORANGE:
                return ModBlocks.STAIRS_WOOL_ORANGE;
            case PINK:
                return ModBlocks.STAIRS_WOOL_PINK;
            case PURPLE:
                return ModBlocks.STAIRS_WOOL_PURPLE;
            case RED:
                return ModBlocks.STAIRS_WOOL_RED;
            case SILVER:
                return ModBlocks.STAIRS_WOOL_SILVER;
            case YELLOW:
                return ModBlocks.STAIRS_WOOL_YELLOW;
            case WHITE:
            default:
                return ModBlocks.STAIRS_WOOL_WHITE;
        }
    }
}
