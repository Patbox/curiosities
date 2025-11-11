package eu.pb4.curiosities.item;

import eu.pb4.curiosities.other.CuriositiesSoundEvents;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class SlimeBucketItem extends Item implements PolymerItem {
    private final EntityType<? extends Slime> type;

    public SlimeBucketItem(EntityType<? extends Slime> type, Properties properties) {
        super(properties);
        this.type = type;
        UseEntityCallback.EVENT.register((player, level, interactionHand, entity, entityHitResult) -> {
            var stack = player.getItemInHand(interactionHand);

            if (stack.is(Items.BUCKET) && entity.isAlive() && entity.getType() == type && entity instanceof Slime slimeEntity && slimeEntity.getSize() == 1) {
                entity.playSound(CuriositiesSoundEvents.ITEM_BUCKET_SLIME_FILL, 1.0F, 1.0F);
                var bucketStack = this.getDefaultInstance();
                Bucketable.saveDefaultDataToBucketTag(slimeEntity, bucketStack);
                CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, nbt -> {
                    nbt.putInt("Size", slimeEntity.getSize());
                });
                var handStack = ItemUtils.createFilledResult(stack, player, bucketStack, false);
                player.setItemInHand(interactionHand, handStack);
                if (!level.isClientSide()) {
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, bucketStack);
                }

                entity.discard();
                return InteractionResult.SUCCESS_SERVER;
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        spawnMob(context.getPlayer(), context.getLevel(), context.getItemInHand(), context.getClickedPos().relative(context.getClickedFace()));
        if (context.getPlayer() != null) {
            var bucketStack = Items.BUCKET.getDefaultInstance();

            var handStack = ItemUtils.createFilledResult(context.getItemInHand(), context.getPlayer(), bucketStack, false);
            context.getPlayer().setItemInHand(context.getHand(), handStack);
        }

        return InteractionResult.SUCCESS_SERVER;
    }

    public void spawnMob(@Nullable LivingEntity entity, Level level, ItemStack stack, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            Slime mob = this.type.create(serverLevel, EntityType.createDefaultStackConfig(serverLevel, stack, null), pos, EntitySpawnReason.BUCKET, true, false);
            var customData = stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY).copyTag();
            Bucketable.loadDefaultDataFromBucketTag(mob, customData);
            mob.setSize(customData.getIntOr("Size", 1), false);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), CuriositiesSoundEvents.ITEM_BUCKET_SLIME_EMPTY, SoundSource.PLAYERS);

            serverLevel.addFreshEntityWithPassengers(mob);
            mob.playAmbientSound();


            level.gameEvent(entity, GameEvent.ENTITY_PLACE, pos);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (slot == null || slot.getType() != EquipmentSlot.Type.HAND) {
            return;
        }

        var chunkPos = entity.chunkPosition();

        if (WorldgenRandom.seedSlimeChunk(chunkPos.x, chunkPos.z, level.getSeed(), 987234911L).nextInt(10) == 0) {
            stack.set(CuriositiesDataComponents.SLIME_ACTIVE, Unit.INSTANCE);
        } else {
            stack.remove(CuriositiesDataComponents.SLIME_ACTIVE);
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        PolymerItem.super.modifyBasePolymerItemStack(out, stack, context);
        out.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(stack.has(CuriositiesDataComponents.SLIME_ACTIVE)), List.of(), List.of()));
    }
}
