package eu.pb4.curiosities.block;


import eu.pb4.curiosities.mixin.PropertiesAccessor;
import eu.pb4.curiosities.other.CuriositiesSoundEvents;
import eu.pb4.curiosities.other.OptionalDirection8;
import eu.pb4.factorytools.api.block.CustomBreakingParticleBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Set;

public class ElevatorBlock extends Block implements FactoryBlock, CustomBreakingParticleBlock {
    public static final EnumProperty<OptionalDirection8> FACING = EnumProperty.create("facing", OptionalDirection8.class);

    private ParticleOptions breakingParticle;
    private final ItemStack model;
    private final ItemStack modelForward;
    private final ItemStack modelCorner;

    public ElevatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, OptionalDirection8.NONE));

        var id = ((PropertiesAccessor) properties).getId().identifier();

        this.model = ItemDisplayElementUtil.getModel(id.withPrefix("block/"));
        this.modelForward = ItemDisplayElementUtil.getModel(id.withPrefix("block/").withSuffix("_forward"));
        this.modelCorner = ItemDisplayElementUtil.getModel(id.withPrefix("block/").withSuffix("_corner"));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        var offset = hitResult.getLocation().subtract(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).multiply(2, 2, 2);

        if (!Mth.equal(offset.y, 1)) {
            return InteractionResult.PASS;
        }

        OptionalDirection8 dir = state.getValue(FACING);

        for (var posDir : OptionalDirection8.values()) {
            if (Math.abs(offset.x - posDir.getStepX()) <= 0.5 && Math.abs(offset.z - posDir.getStepZ()) <= 0.5) {
                dir = posDir;
                break;
            }
        }

        if (state.getValue(FACING) == dir) {
            return InteractionResult.PASS;
        }

        level.setBlockAndUpdate(pos, state.setValue(FACING, dir));
        level.playSound(null, pos, CuriositiesSoundEvents.BLOCK_ELEVATOR_MODIFY, SoundSource.BLOCKS, 1f, 1f);

        return InteractionResult.SUCCESS_SERVER;
    }

    public static void handleRequest(Entity entity, ServerLevel level, BlockState state, BlockPos pos, int dir) {
        var mut = pos.mutable();

        var dimension = entity.getDimensions(entity.getPose());

        while (true) {
            mut.move(0, dir,0);
            if (level.isOutsideBuildHeight(mut)) {
                return;
            }
            var candidate = level.getBlockState(mut);
            if (!candidate.is(state.getBlock())) {
                continue;
            }
            if (!level.noCollision(entity, dimension.makeBoundingBox(mut.getX() + 0.5f, mut.getY() + 1, mut.getZ() + 0.5))) {
                continue;
            }
            state = candidate;
            break;
        }
        var oldPos = entity.position();

        var yaw = state.getValue(FACING).yaw();

        entity.teleportTo(level, mut.getX() + 0.5, mut.getY() + 1, mut.getZ() + 0.5,
                yaw.isEmpty() ? Set.of(Relative.X_ROT, Relative.Y_ROT) : Set.of(Relative.X_ROT), (float) yaw.orElse(0), 0, false);
        level.gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(entity));

        if (!entity.isSilent()) {
            level.playSound(null, oldPos.x, oldPos.y, oldPos.z, SoundEvents.PLAYER_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, entity.getSoundSource(), 1.0F, 1.0F);
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    public ParticleOptions getBreakingParticle(BlockState blockState) {
        if (this.breakingParticle == null) {
            this.breakingParticle = new ItemParticleOption(ParticleTypes.ITEM, ItemDisplayElementUtil.getModel(this.asItem()));
        }
        return this.breakingParticle;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    public final class Model extends BlockModel {
        private final ItemDisplayElement base;

        public Model(BlockState state) {
            this.base = ItemDisplayElementUtil.createSimple();
            base.setScale(new Vector3f(2f));
            this.updateModel(state);
            this.addElement(base);
        }

        @Override
        public void notifyUpdate(HolderAttachment.UpdateType updateType) {
            super.notifyUpdate(updateType);
            if (updateType == BlockAwareAttachment.BLOCK_STATE_UPDATE) {
                var x = this.base.getTeleportDuration();
                this.base.setTeleportDuration(0);
                updateModel(this.blockState());
                this.tick();
                this.base.setTeleportDuration(x);
            }
        }

        private void updateModel(BlockState state) {
            var dir = state.getValue(FACING);
            if (dir == OptionalDirection8.NONE) {
                this.base.setItem(model);
                this.base.setYaw(0);
                return;
            }
            this.base.setItem(dir.isForward() ? modelForward : modelCorner);
            this.base.setYaw(dir.getModelYaw());
        }
    }
}
