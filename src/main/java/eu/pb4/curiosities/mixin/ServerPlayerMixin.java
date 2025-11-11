package eu.pb4.curiosities.mixin;

import com.mojang.authlib.GameProfile;
import eu.pb4.curiosities.block.ElevatorBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Shadow private Input lastClientInput;

    @Shadow public abstract ServerLevel level();

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "setLastClientInput", at = @At("HEAD"))
    private void onClientInput(Input lastClientInput, CallbackInfo ci) {
        if (this.lastClientInput.jump() == lastClientInput.jump()
                && this.lastClientInput.shift() == lastClientInput.shift()
                || lastClientInput.shift() == lastClientInput.jump()
        ) {
            return;
        }

        var onPos = this.getOnPos();
        var stepping = this.level().getBlockState(onPos);
        if (!(stepping.getBlock() instanceof ElevatorBlock)) {
            return;
        }

        ElevatorBlock.handleRequest(this, this.level(), stepping, onPos, lastClientInput.shift() ? -1 : 1);
    }
}
