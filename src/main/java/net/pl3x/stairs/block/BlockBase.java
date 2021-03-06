package net.pl3x.stairs.block;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.pl3x.stairs.Stairs;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BlockBase extends Block {
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<EnumHalf> HALF = PropertyEnum.create("half", EnumHalf.class);
    public static final PropertyEnum<EnumShape> SHAPE = PropertyEnum.create("shape", EnumShape.class);
    public static final AxisAlignedBB AABB_SLAB_TOP = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB AABB_QTR_TOP_WEST = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 1.0D);
    public static final AxisAlignedBB AABB_QTR_TOP_EAST = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB AABB_QTR_TOP_NORTH = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    public static final AxisAlignedBB AABB_QTR_TOP_SOUTH = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB AABB_OCT_TOP_NW = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 0.5D);
    public static final AxisAlignedBB AABB_OCT_TOP_NE = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
    public static final AxisAlignedBB AABB_OCT_TOP_SW = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D);
    public static final AxisAlignedBB AABB_OCT_TOP_SE = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
    public static final AxisAlignedBB AABB_SLAB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    public static final AxisAlignedBB AABB_QTR_BOT_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 1.0D);
    public static final AxisAlignedBB AABB_QTR_BOT_EAST = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
    public static final AxisAlignedBB AABB_QTR_BOT_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D);
    public static final AxisAlignedBB AABB_QTR_BOT_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
    public static final AxisAlignedBB AABB_OCT_BOT_NW = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 0.5D);
    public static final AxisAlignedBB AABB_OCT_BOT_NE = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D);
    public static final AxisAlignedBB AABB_OCT_BOT_SW = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 0.5D, 0.5D, 1.0D);
    public static final AxisAlignedBB AABB_OCT_BOT_SE = new AxisAlignedBB(0.5D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);

    private final String name;

    public BlockBase(Material material, String name) {
        super(material);

        this.name = name;

        setUnlocalizedName(name);
        setRegistryName(name);

        setDefaultState(blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(HALF, EnumHalf.BOTTOM)
                .withProperty(SHAPE, EnumShape.STRAIGHT));

        setCreativeTab(ModBlocks.TAB_STAIRS);
        ModBlocks.__BLOCKS__.add(this);
    }

    public void registerItemModel(Item item) {
        Stairs.proxy.registerItemRenderer(item, 0, name);
    }

    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

    public Item createItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState) {
        if (!isActualState) {
            state = this.getActualState(state, world, pos);
        }
        for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state)) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
        }
    }

    private static List<AxisAlignedBB> getCollisionBoxList(IBlockState state) {
        List<AxisAlignedBB> list = Lists.newArrayList();
        boolean flag = state.getValue(HALF) == EnumHalf.TOP;
        list.add(flag ? AABB_SLAB_TOP : AABB_SLAB_BOTTOM);
        EnumShape shape = state.getValue(SHAPE);
        if (shape == EnumShape.STRAIGHT || shape == EnumShape.INNER_LEFT || shape == EnumShape.INNER_RIGHT) {
            list.add(getCollQuarterBlock(state));
        }
        if (shape != EnumShape.STRAIGHT) {
            list.add(getCollEighthBlock(state));
        }
        return list;
    }

    private static AxisAlignedBB getCollQuarterBlock(IBlockState state) {
        boolean flag = state.getValue(HALF) == EnumHalf.TOP;
        switch (state.getValue(FACING)) {
            case NORTH:
            default:
                return flag ? AABB_QTR_BOT_NORTH : AABB_QTR_TOP_NORTH;
            case SOUTH:
                return flag ? AABB_QTR_BOT_SOUTH : AABB_QTR_TOP_SOUTH;
            case WEST:
                return flag ? AABB_QTR_BOT_WEST : AABB_QTR_TOP_WEST;
            case EAST:
                return flag ? AABB_QTR_BOT_EAST : AABB_QTR_TOP_EAST;
        }
    }

    private static AxisAlignedBB getCollEighthBlock(IBlockState state) {
        EnumFacing facing = state.getValue(FACING);
        EnumFacing facing1;
        switch (state.getValue(SHAPE)) {
            case OUTER_LEFT:
            default:
                facing1 = facing;
                break;
            case OUTER_RIGHT:
                facing1 = facing.rotateY();
                break;
            case INNER_RIGHT:
                facing1 = facing.getOpposite();
                break;
            case INNER_LEFT:
                facing1 = facing.rotateYCCW();
        }
        boolean flag = state.getValue(HALF) == EnumHalf.TOP;
        switch (facing1) {
            case NORTH:
            default:
                return flag ? AABB_OCT_BOT_NW : AABB_OCT_TOP_NW;
            case SOUTH:
                return flag ? AABB_OCT_BOT_SE : AABB_OCT_TOP_SE;
            case WEST:
                return flag ? AABB_OCT_BOT_SW : AABB_OCT_TOP_SW;
            case EAST:
                return flag ? AABB_OCT_BOT_NE : AABB_OCT_TOP_NE;
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        state = getActualState(state, world, pos);
        if (face.getAxis() == EnumFacing.Axis.Y) {
            return face == EnumFacing.UP == (state.getValue(HALF) == EnumHalf.TOP) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        } else {
            EnumShape shape = state.getValue(SHAPE);
            if (shape != EnumShape.OUTER_LEFT && shape != EnumShape.OUTER_RIGHT) {
                EnumFacing enumfacing = state.getValue(FACING);
                switch (shape) {
                    case INNER_RIGHT:
                        return enumfacing != face && enumfacing != face.rotateYCCW() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
                    case INNER_LEFT:
                        return enumfacing != face && enumfacing != face.rotateY() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
                    case STRAIGHT:
                        return enumfacing == face ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
                    default:
                        return BlockFaceShape.UNDEFINED;
                }
            } else {
                return BlockFaceShape.UNDEFINED;
            }
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return state.getValue(HALF) == EnumHalf.TOP;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        state = state.withProperty(FACING, placer.getHorizontalFacing()).withProperty(SHAPE, EnumShape.STRAIGHT);
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? state.withProperty(HALF, EnumHalf.BOTTOM) : state.withProperty(HALF, EnumHalf.TOP);
    }

    @Override
    @Nullable
    public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end) {
        List<RayTraceResult> list = Lists.newArrayList();
        for (AxisAlignedBB aabb : getCollisionBoxList(getActualState(blockState, world, pos))) {
            list.add(rayTrace(pos, start, end, aabb));
        }
        RayTraceResult result = null;
        double d1 = 0.0D;
        for (RayTraceResult raytraceresult : list) {
            if (raytraceresult != null) {
                double d0 = raytraceresult.hitVec.squareDistanceTo(end);
                if (d0 > d1) {
                    result = raytraceresult;
                    d1 = d0;
                }
            }
        }
        return result;
    }

    /*
    DEC BIN  FACING HALF
    0   0000 south  bottom
    1   0001 south  top
    2   0010 west   bottom
    3   0011 west   top
    4   0100 north  bottom
    5   0101 north  top
    6   0110 east   bottom
    7   0111 east   top
        ││││
        │││└─── half        (bottom[0], top[1])
        │└└──── facing      (south[00], west[01], north[10], east[11])
        └────── [not used]
    */

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(HALF, (meta & 1) > 0 ? EnumHalf.TOP : EnumHalf.BOTTOM)
                .withProperty(FACING, EnumFacing.getHorizontal(meta >> 1));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        if (state.getValue(HALF) == EnumHalf.TOP) {
            i |= 1;
        }
        i |= state.getValue(FACING).getHorizontalIndex() << 1;
        return i;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(SHAPE, getStairsShape(state, world, pos));
    }

    public static EnumShape getStairsShape(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        IBlockState state1 = world.getBlockState(pos.offset(facing));
        if (isBlockStairs(state1) && state.getValue(HALF) == state1.getValue(HALF)) {
            EnumFacing facing1 = state1.getValue(FACING);
            if (facing1.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, world, pos, facing1.getOpposite())) {
                if (facing1 == facing.rotateYCCW()) {
                    return EnumShape.OUTER_LEFT;
                }
                return EnumShape.OUTER_RIGHT;
            }
        }
        IBlockState state2 = world.getBlockState(pos.offset(facing.getOpposite()));
        if (isBlockStairs(state2) && state.getValue(HALF) == state2.getValue(HALF)) {
            EnumFacing facing2 = state2.getValue(FACING);
            if (facing2.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, world, pos, facing2)) {
                if (facing2 == facing.rotateYCCW()) {
                    return EnumShape.INNER_LEFT;
                }
                return EnumShape.INNER_RIGHT;
            }
        }
        return EnumShape.STRAIGHT;
    }

    private static boolean isDifferentStairs(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IBlockState iblockstate = world.getBlockState(pos.offset(facing));
        return !isBlockStairs(iblockstate) || iblockstate.getValue(FACING) != state.getValue(FACING) || iblockstate.getValue(HALF) != state.getValue(HALF);
    }

    public static boolean isBlockStairs(IBlockState state) {
        return state.getBlock() instanceof BlockBase || state.getBlock() instanceof BlockStairs;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        EnumFacing enumfacing = state.getValue(FACING);
        EnumShape shape = state.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (enumfacing.getAxis() == EnumFacing.Axis.Z) {
                    switch (shape) {
                        case OUTER_LEFT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.OUTER_LEFT);
                        case INNER_RIGHT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.INNER_LEFT);
                        case INNER_LEFT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.INNER_RIGHT);
                        default:
                            return state.withRotation(Rotation.CLOCKWISE_180);
                    }
                }
                break;
            case FRONT_BACK:
                if (enumfacing.getAxis() == EnumFacing.Axis.X) {
                    switch (shape) {
                        case OUTER_LEFT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.OUTER_RIGHT);
                        case OUTER_RIGHT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.OUTER_LEFT);
                        case INNER_RIGHT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.INNER_RIGHT);
                        case INNER_LEFT:
                            return state.withRotation(Rotation.CLOCKWISE_180).withProperty(SHAPE, EnumShape.INNER_LEFT);
                        case STRAIGHT:
                            return state.withRotation(Rotation.CLOCKWISE_180);
                    }
                }
        }
        return super.withMirror(state, mirror);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, HALF, SHAPE);
    }

    @Override
    public boolean canEntitySpawn(IBlockState state, Entity entity) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        if (ForgeModContainer.disableStairSlabCulling) {
            return super.doesSideBlockRendering(state, world, pos, face);
        }
        if (state.isOpaqueCube()) {
            return true;
        }
        state = getActualState(state, world, pos);
        EnumHalf half = state.getValue(HALF);
        EnumFacing side = state.getValue(FACING);
        EnumShape shape = state.getValue(SHAPE);
        if (face == EnumFacing.UP) {
            return half == EnumHalf.TOP;
        }
        if (face == EnumFacing.DOWN) {
            return half == EnumHalf.BOTTOM;
        }
        if (shape == EnumShape.OUTER_LEFT || shape == EnumShape.OUTER_RIGHT) {
            return false;
        }
        if (face == side) {
            return true;
        }
        if (shape == EnumShape.INNER_LEFT && face.rotateY() == side) {
            return true;
        }
        if (shape == EnumShape.INNER_RIGHT && face.rotateYCCW() == side) {
            return true;
        }
        return false;
    }

    public enum EnumHalf implements IStringSerializable {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        EnumHalf(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public enum EnumShape implements IStringSerializable {
        STRAIGHT("straight"),
        INNER_LEFT("inner_left"),
        INNER_RIGHT("inner_right"),
        OUTER_LEFT("outer_left"),
        OUTER_RIGHT("outer_right");

        private final String name;

        EnumShape(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}
