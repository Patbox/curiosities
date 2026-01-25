package eu.pb4.curiosities.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import eu.pb4.curiosities.item.CuriositiesItems;
import eu.pb4.curiosities.other.EntityLightEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    @Nullable
    private BlockPos lastLightPos;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Inject(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", shift = At.Shift.BEFORE), cancellable = true)
    private void handleSlimeBoots(double fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, @Local LocalIntRef damage) {
        var stack = this.getItemBySlot(EquipmentSlot.FEET);
        if (!stack.is(CuriositiesItems.SLIME_BOOTS)) {
            return;
        }
        var previousDamage = stack.getDamageValue();
        var maxDamage = stack.getMaxDamage();
        stack.hurtAndBreak(damage.get(), (LivingEntity) (Object) this, EquipmentSlot.FEET);
        if (stack.isEmpty()) {
            damage.set((maxDamage - previousDamage) - damage.get());
        } else {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void handleMinerHelmet(CallbackInfo ci) {
        var engine = (EntityLightEngine) ((LevelLightEngineAccessor) this.level().getLightEngine()).getBlockEngine();

        if (engine == null) {
            return;
        }

        var hasMinerHelmet = this.getItemBySlot(EquipmentSlot.HEAD).is(CuriositiesItems.MINING_HELMET);
        var pos = BlockPos.containing(this.getEyePosition());
        if (hasMinerHelmet && !pos.equals(this.lastLightPos)) {
            engine.curiosities$setLightLevel(pos, 10);
            if (this.lastLightPos != null) {
                engine.curiosities$setLightLevel(this.lastLightPos, 0);
                this.level().getLightEngine().checkBlock(this.lastLightPos);
            }
            this.level().getLightEngine().checkBlock(pos);
            if (this.level().getLightEngine() instanceof ThreadedLevelLightEngine levelLightEngine) {
                levelLightEngine.tryScheduleUpdate();
            }
            this.lastLightPos = pos;
        } else if (!hasMinerHelmet && this.lastLightPos != null) {
            engine.curiosities$setLightLevel(this.lastLightPos, 0);
            this.level().getLightEngine().checkBlock(this.lastLightPos);
            if (this.level().getLightEngine() instanceof ThreadedLevelLightEngine levelLightEngine) {
                levelLightEngine.tryScheduleUpdate();
            }
            this.lastLightPos = null;
        }
    }

    @Inject(method = "onRemoval", at = @At("HEAD"))
    private void clearOldLight(RemovalReason reason, CallbackInfo ci) {
        var engine = (EntityLightEngine) ((LevelLightEngineAccessor) this.level().getLightEngine()).getBlockEngine();

        if (engine == null) {
            return;
        }

        if (this.lastLightPos != null) {
            engine.curiosities$setLightLevel(this.lastLightPos, 0);
            this.level().getLightEngine().checkBlock(this.lastLightPos);
            if (this.level().getLightEngine() instanceof ThreadedLevelLightEngine levelLightEngine) {
                levelLightEngine.tryScheduleUpdate();
            }
            this.lastLightPos = null;
        }
    }
}
