package net.pl3x.stairs.block.stairs;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.pl3x.stairs.block.BlockBase;

public class StairsHay extends BlockBase {
    public StairsHay() {
        super(Material.GRASS, "stairs_hay");
        setSoundType(SoundType.PLANT);
        setHardness(0.5F);
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.YELLOW;
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        entity.fall(fallDistance, 0.2F);
    }
}
