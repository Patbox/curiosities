package eu.pb4.curiosities.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.curiosities.item.CuriositiesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    private boolean bypassFallDistanceCheck;

    @Shadow
    protected abstract void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos);

    @Shadow
    public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract SoundSource getSoundSource();

    @Shadow
    public double fallDistance;

    @WrapWithCondition(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;updateEntityMovementAfterFallOn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;)V"))
    private boolean handleSlimeBoots(Block instance, BlockGetter level, Entity entity, @Local BlockState block, @Local(ordinal = 1) Vec3 activeMovement, @Local BlockPos pos) {
        //noinspection ConstantValue
        if (!(((Object) this) instanceof LivingEntity livingEntity) || block.is(Blocks.SLIME_BLOCK) || livingEntity.isShiftKeyDown() || !(level instanceof ServerLevel serverLevel)) {
            this.bypassFallDistanceCheck = false;
            return true;
        }
        var boots = livingEntity.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.is(CuriositiesItems.SLIME_BOOTS)) {
            this.bypassFallDistanceCheck = false;
            return true;
        }

        var vec3 = livingEntity.getDeltaMovement();
        if (vec3.y >= 0 || (this.fallDistance < 1.4f && !this.bypassFallDistanceCheck)) {
            this.bypassFallDistanceCheck = false;
            return true;
        }
        livingEntity.setDeltaMovement(vec3.x, -vec3.y * 0.95f, vec3.z);
        var delta = Math.max(livingEntity.getDeltaMovement().y, 0);
        if (delta > 0.01) {
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SLIME_BLOCK_FALL, this.getSoundSource(), 1, 1);
            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.defaultBlockState()), false, false,
                    this.getX(), this.getY(), this.getZ(), 6, 0.1f, 0.1f, 0.1f, 0.2f);
            if (livingEntity instanceof ServerPlayer player && delta > 0.01) {
                var pDelta = Math.max(player.getKnownMovement().y * -0.95f, 0);

                this.bypassFallDistanceCheck = true;
                if (pDelta > 0.01) {
                    player.connection.send(new ClientboundTeleportEntityPacket(player.getId(),
                            new PositionMoveRotation(Vec3.ZERO, new Vec3(0, pDelta, 0), 0, 0),
                            Set.of(Relative.DELTA_X, Relative.DELTA_Z, Relative.X, Relative.Y, Relative.Z, Relative.X_ROT, Relative.Y_ROT),
                            false
                    ));
                }
                this.checkFallDamage(activeMovement.y, true, block, pos);
            }
        } else {
            this.bypassFallDistanceCheck = false;
        }
        return false;
    }
}
