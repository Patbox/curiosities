package eu.pb4.curiosities.block;

import com.mojang.serialization.MapCodec;
import eu.pb4.factorytools.api.block.CustomBreakingParticleBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import static eu.pb4.curiosities.ModInit.id;

public class CrossRailBlock extends BaseRailBlock implements FactoryBlock, PolymerTexturedBlock, CustomBreakingParticleBlock {
    private static final BlockState STATE = PolymerBlockResourceUtils.requestEmpty(BlockModelType.ACTIVE_PRESSURE_PLATE);
    private static final BlockState STATE_WATERLOGGED = PolymerBlockResourceUtils.requestEmpty(BlockModelType.KELP_BLOCK);

    public static final Property<RailShape> RAIL_SHAPE = EnumProperty.create("shape", RailShape.class, RailShape.NORTH_SOUTH, RailShape.EAST_WEST);

    private ParticleOptions breakingParticle;

    protected CrossRailBlock(Properties properties) {
        super(true, properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RAIL_SHAPE,WATERLOGGED);
    }

    @Override
    protected MapCodec<? extends BaseRailBlock> codec() {
        return null;
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return RAIL_SHAPE;
    }

    public static BlockState adjustState(BlockState state, AbstractMinecart minecart, BlockPos pos, @Nullable BlockPos previousDirectionalRailPos) {
        var dir = previousDirectionalRailPos != null ? Direction.getNearest(pos.subtract(previousDirectionalRailPos), minecart.getBehavior().getMotionDirection()) : minecart.getBehavior().getMotionDirection();

        if (dir.getAxis() == Direction.Axis.Z) {
            return state.setValue(RAIL_SHAPE, RailShape.NORTH_SOUTH);
        }
        return state.setValue(RAIL_SHAPE, RailShape.EAST_WEST);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    @Override
    public ParticleOptions getBreakingParticle(BlockState blockState) {
        if (this.breakingParticle == null) {
            this.breakingParticle = new ItemParticleOption(ParticleTypes.ITEM, ItemDisplayElementUtil.getModel(this.asItem()));
        }
        return this.breakingParticle;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return blockState.getValue(WATERLOGGED) ? STATE_WATERLOGGED : STATE;
    }

    private static final class Model extends BlockModel {
        private static final ItemStack MODEL = ItemDisplayElementUtil.getModel(id("block/cross_rail"));

        public Model(BlockState state) {
            var base = ItemDisplayElementUtil.createSimple(MODEL);
            this.addElement(base);
        }
    }
}
