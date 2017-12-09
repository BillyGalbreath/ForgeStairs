package net.pl3x.stairs.block.stairs;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.pl3x.stairs.block.BlockBase;

public class StairsBasic extends BlockBase {
    private final MapColor mapColor;

    public StairsBasic(Material material, String name, MapColor mapColor) {
        super(material, name);
        this.mapColor = mapColor;
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return mapColor;
    }

    @Override
    public StairsBasic setSoundType(SoundType sound) {
        super.setSoundType(sound);
        return this;
    }

    @Override
    public StairsBasic setLightOpacity(int opacity) {
        super.setLightOpacity(opacity);
        return this;
    }

    @Override
    public StairsBasic setLightLevel(float value) {
        super.setLightLevel(value);
        return this;
    }

    @Override
    public StairsBasic setResistance(float resistance) {
        super.setResistance(resistance);
        return this;
    }

    @Override
    public StairsBasic setHardness(float hardness) {
        super.setHardness(hardness);
        return this;
    }

    public StairsBasic setSlipperiness(float slipperiness) {
        super.setDefaultSlipperiness(slipperiness);
        return this;
    }

    @Override
    public StairsBasic disableStats() {
        super.disableStats();
        return this;
    }
}
