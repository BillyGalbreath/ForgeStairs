package net.pl3x.stairs.block.stairs;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.pl3x.stairs.block.BlockBase;
import net.pl3x.stairs.block.ModBlocks;

public class BlockStairsCraftingTable extends BlockBase {
    public BlockStairsCraftingTable() {
        super(Material.WOOD, "stairs_crafting_table");
        setSoundType(SoundType.WOOD);
        setHardness(2.5F);
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return MapColor.WOOD;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.displayGui(new CraftingTable(world, pos));
            player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
        }
        return true;
    }

    private class CraftingTable extends BlockWorkbench.InterfaceCraftingTable {
        private final World world;
        private final BlockPos pos;

        private CraftingTable(World world, BlockPos pos) {
            super(world, pos);
            this.world = world;
            this.pos = pos;
        }

        @Override
        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player) {
            return new ContainerCraftingTable(playerInventory, world, pos);
        }
    }

    private class ContainerCraftingTable extends ContainerWorkbench {
        private World world;
        private BlockPos pos;

        private ContainerCraftingTable(InventoryPlayer playerInventory, World world, BlockPos pos) {
            super(playerInventory, world, pos);
            this.world = world;
            this.pos = pos;
        }

        @Override
        public boolean canInteractWith(EntityPlayer player) {
            return world.getBlockState(pos).getBlock() == ModBlocks.STAIRS_CRAFTING_TABLE &&
                    player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
        }
    }
}
