package eu.pb4.curiosities.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.curiosities.block.CrossRailBlock;
import eu.pb4.curiosities.block.CuriositiesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartBehavior;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
    @Unique
    private BlockPos previousDirectionalRailPos = this.minecart.getOnPos();
    protected NewMinecartBehaviorMixin(AbstractMinecart minecart) {
        super(minecart);
    }

    @ModifyExpressionValue(method = {"moveAlongTrack", "adjustToRails", "stepAlongTrack"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState changeDirectionality(BlockState original, @Local BlockPos pos) {
        if (original.is(CuriositiesBlocks.CROSS_RAIL)) {
            return CrossRailBlock.adjustState(original, this.minecart, pos, this.minecart.isOnRails() ? this.previousDirectionalRailPos : null);
        }
        return original;
    }

    @Inject(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;resetFallDistance()V"))
    private void storePosition(ServerLevel level, CallbackInfo ci, @Local BlockPos pos, @Local BlockState state) {
        if (!state.is(CuriositiesBlocks.CROSS_RAIL)) {
            this.previousDirectionalRailPos = pos;
        }
    }
}
