package eu.pb4.curiosities.block;


import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Set;

public class ElevatorBlock extends Block implements FactoryBlock {
    public ElevatorBlock(Properties properties) {
        super(properties);
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
            if (candidate != state) {
                continue;
            }
            if (!level.noCollision(entity, dimension.makeBoundingBox(mut.getX() + 0.5f, mut.getY() + 1, mut.getZ() + 0.5))) {
                continue;
            }
            break;
        }
        var oldPos = entity.position();

        entity.teleportTo(level, mut.getX() + 0.5, mut.getY() + 1, mut.getZ() + 0.5, Set.of(Relative.X_ROT, Relative.Y_ROT), 0, 0, false);
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
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    public static final class Model extends BlockModel {
        public Model(BlockState state) {
            var model = ItemDisplayElementUtil.createSimple(state.getBlock().asItem());
            model.setScale(new Vector3f(2f));
            this.addElement(model);
        }
    }
}
