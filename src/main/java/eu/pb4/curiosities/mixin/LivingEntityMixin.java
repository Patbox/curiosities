package eu.pb4.curiosities.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import eu.pb4.curiosities.item.CuriositiesItems;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow protected abstract double getEffectiveGravity();

    @Inject(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.BEFORE), cancellable = true)
    private void handleSlimeBoots(double fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, @Local LocalIntRef damage) {
        var stack = this.getItemBySlot(EquipmentSlot.FEET);
        if (!stack.is(CuriositiesItems.SLIME_BOOTS)) {
            return;
        }
        var previousDamage = stack.getDamageValue();
        var maxDamage = stack.getMaxDamage();
        stack.hurtAndBreak(damage.get(), (LivingEntity) (Object) this, EquipmentSlot.FEET);
        float bounce = 1;
        if (stack.isEmpty()) {
            bounce = (maxDamage - previousDamage) / (float) damage.get();
            damage.set((maxDamage - previousDamage) - damage.get());
        } else {
            cir.setReturnValue(false);
        }
        this.playSound(SoundEvents.SLIME_BLOCK_FALL, 1.0F, 1.0F);

        var velocity = this.getDeltaMovement();
        if (velocity.y < 0.0) {
            this.setDeltaMovement(velocity.x, -velocity.y * bounce, velocity.z);
            if (((Object) this) instanceof ServerPlayer serverPlayer) {
                var gravity = this.getEffectiveGravity();
                var time = Math.sqrt(fallDistance / gravity * 2);

                serverPlayer.connection.send(new ClientboundPlayerPositionPacket(0,
                        new PositionMoveRotation(Vec3.ZERO, new Vec3(0, time * gravity * Math.pow(0.98, time), 0), 0, 0),
                        Set.of(Relative.DELTA_X, Relative.DELTA_Z, Relative.X, Relative.Y, Relative.Z, Relative.X_ROT, Relative.Y_ROT)
                        ));
            }
        }
    }
}
