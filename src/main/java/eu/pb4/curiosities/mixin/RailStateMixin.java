package eu.pb4.curiosities.mixin;

import eu.pb4.curiosities.block.CuriositiesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RailState.class)
public class RailStateMixin {
    @Shadow @Final private BaseRailBlock block;

    @Shadow @Final private List<BlockPos> connections;

    @Shadow @Final private BlockPos pos;

    @Inject(method = "updateConnections", at = @At("HEAD"), cancellable = true)
    private void crossRailConnections(RailShape shape, CallbackInfo ci) {
        if (this.block != CuriositiesBlocks.CROSS_RAIL) {
            return;
        }
        this.connections.clear();
        this.connections.add(this.pos.north());
        this.connections.add(this.pos.south());
        this.connections.add(this.pos.west());
        this.connections.add(this.pos.east());
        ci.cancel();
    }
}
