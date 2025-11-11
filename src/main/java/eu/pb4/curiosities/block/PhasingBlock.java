package eu.pb4.curiosities.block;

import com.mojang.serialization.MapCodec;
import eu.pb4.curiosities.item.CuriositiesItems;
import eu.pb4.curiosities.other.CuriositiesSoundEvents;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;

public class PhasingBlock extends BaseEntityBlock implements FactoryBlock {
    public static final EnumProperty<Phase> PHASE = EnumProperty.create("phase", Phase.class);
    public static final IntegerProperty LIGHT = IntegerProperty.create("light_level", 0, 15);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public PhasingBlock(Properties properties) {
        super(properties.lightLevel(x -> x.getValue(LIGHT)));
        this.registerDefaultState(this.defaultBlockState().setValue(PHASE, Phase.INACTIVE).setValue(LIGHT, 0).setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PHASE, POWERED, LIGHT);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        var stack = player.getMainHandItem();

        if (state.getValue(PHASE) == Phase.INACTIVE && level instanceof ServerLevel serverLevel && !stack.is(CuriositiesItems.PHASER) && !(stack.getItem() instanceof BlockItem)) {
            this.tick(state.setValue(PHASE, Phase.WAITING), serverLevel, pos, level.getRandom());
            return InteractionResult.SUCCESS;
        }

        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        var powered = level.hasNeighborSignal(pos);

        if (state.getValue(POWERED) != powered) {
            if (powered) {
                level.scheduleTick(pos.immutable(), state.getBlock(), 1);
            }

            if (state.getValue(PHASE) == Phase.INACTIVE) {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, powered).setValue(PHASE, Phase.WAITING));
            } else {
                level.setBlockAndUpdate(pos, state.setValue(POWERED, powered));
            }
        }

        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (neighborState.getBlock() instanceof PhasingBlock && neighborState.getValue(PHASE) == Phase.ACTIVE && state.getValue(PHASE) == Phase.INACTIVE) {
            scheduledTickAccess.scheduleTick(pos.immutable(), state.getBlock(), 2);
            return state.setValue(PHASE, Phase.WAITING);
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        switch (state.getValue(PHASE)) {
            case WAITING -> {
                level.setBlockAndUpdate(pos, state.setValue(PHASE, Phase.ACTIVE));
                level.scheduleTick(pos.immutable(), state.getBlock(), 5 * 20);
                level.playSound(null, pos, CuriositiesSoundEvents.BLOCK_PHASING_POP_OUT, SoundSource.BLOCKS);
                spawnShapedParticles(new DustColorTransitionOptions(0x880000, 0xE54CFF, 1.5f), state, level, pos, random);
            }
            case ACTIVE -> {
                level.setBlockAndUpdate(pos, state.setValue(PHASE, Phase.COOLDOWN));
                level.scheduleTick(pos.immutable(), state.getBlock(), 2);
                level.playSound(null, pos, CuriositiesSoundEvents.BLOCK_PHASING_POP_IN, SoundSource.BLOCKS);
                spawnShapedParticles(new DustColorTransitionOptions(0xE54CFF, 0xBB0000, 1.5f), state, level, pos, random);
            }
            case COOLDOWN -> level.setBlockAndUpdate(pos, state.setValue(PHASE, Phase.INACTIVE));
        }

        super.tick(state, level, pos, random);
    }

    private void spawnShapedParticles(DustColorTransitionOptions particle, BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        var voxelShape = state.getShape(level, pos);
        voxelShape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            var halfX = (maxX + minX) / 2;
            var halfY = (maxY + minY) / 2;
            var halfZ = (maxZ + minZ) / 2;


            level.sendParticles(particle, pos.getX() + halfX, (double) pos.getY() + halfY, (double) pos.getZ() + halfZ,
                    6, halfX - 0.1, halfY - 0.1, halfZ - 0.1, 2);

        });
    }


    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return blockState.getValue(PHASE) == Phase.ACTIVE ? Blocks.STRUCTURE_VOID.defaultBlockState() : Blocks.BARRIER.defaultBlockState();
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        var items = new ArrayList<>(super.getDrops(state, params));
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PhasingBlockEntity be) {
            items.addAll(be.getVisualState().getDrops(params));
        }

        return items;
    }

    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PhasingBlockEntity be) {
            return be.getVisualState().getDestroyProgress(player, level, pos);
        }

        return 1;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof PhasingBlockEntity be && level instanceof ServerLevel serverLevel) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundLevelEventPacket(2001, pos, getId(be.getVisualState()), false));
            }
            if (!player.isCreative()) {
                super.getDrops(state, new LootParams.Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                        .withOptionalParameter(LootContextParams.BLOCK_ENTITY, null))
                        .forEach(x -> Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY()  + 0.5, pos.getZ() + 0.5, x));
            }

            return be.getVisualState().getBlock().playerWillDestroy(level, pos, be.getVisualState(), player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PhasingBlockEntity(pos, state);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    @Override
    public boolean forceLightUpdates(BlockState blockState) {
        return blockState.getValue(LIGHT) > 0;
    }

    public enum Phase implements StringRepresentable {
        INACTIVE("inactive"),
        WAITING("waiting"),
        COOLDOWN("cooldown"),
        ACTIVE("active");

        private final String name;

        Phase(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public static final class Model extends BlockModel {
        private final BlockDisplayElement base = new BlockDisplayElement();
        private BlockState state = Blocks.STONE.defaultBlockState();

        public Model(BlockState currentState) {
            this.base.setTranslation(new Vector3f(-0.5f));
            this.base.setBlockState(currentState.getValue(PHASE) == Phase.ACTIVE ? Blocks.AIR.defaultBlockState() : this.state);

            this.addElement(base);
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            super.notifyUpdate(updateType);
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                this.base.setBlockState(this.blockState().getValue(PHASE) == Phase.ACTIVE ? Blocks.AIR.defaultBlockState() : this.state);
                this.base.tick();
            }
        }

        public void setVisualState(BlockState blockState) {
            if (this.state == blockState) {
                return;
            }
            this.state = blockState;
            if (this.blockState().getValue(PHASE) != Phase.ACTIVE) {
                this.base.setBlockState(blockState);
                this.base.tick();
            }
        }
    }

}
