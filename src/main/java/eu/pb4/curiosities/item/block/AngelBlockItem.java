package eu.pb4.curiosities.item.block;

import eu.pb4.curiosities.other.CuriositiesUtils;
import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class AngelBlockItem extends FactoryBlockItem {
    public <T extends Block & PolymerBlock> AngelBlockItem(T block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);

        if (!(entity instanceof ServerPlayer player) || slot == null || slot.getType() != EquipmentSlot.Type.HAND) {
            return;
        }

        var hit = AngelBlockItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (!hit.isWorldBorderHit() && hit.getType() != HitResult.Type.MISS) {
            return;
        }

        {
            Vec3 eyePosition = player.getEyePosition();
            Vec3 endPos = eyePosition.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale((player.isShiftKeyDown() ? 0.5 : player.blockInteractionRange() / 2) + 1));
            hit = level.clip(new ClipContext(eyePosition, endPos, net.minecraft.world.level.ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        }

        CuriositiesUtils.addOutlineParticles(level, hit.getBlockPos(), Shapes.block(), new DustParticleOptions(0xFFFFFF, 0.5f));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        BlockHitResult hit;
        {
            Vec3 eyePosition = player.getEyePosition();
            Vec3 endPos = eyePosition.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale((player.isShiftKeyDown() ? 0.5 : player.blockInteractionRange() / 2) + 1));
            hit = level.clip(new ClipContext(eyePosition, endPos, net.minecraft.world.level.ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        }

        if (!hit.isWorldBorderHit() && hit.getType() != HitResult.Type.MISS) {
            return InteractionResult.PASS;
        }

        return this.place(new BlockPlaceContext(player, hand, player.getItemInHand(hand), hit));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        tooltipAdder.accept(Component.literal(" ").append(Component.translatable( this.descriptionId + ".desc")).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public boolean isPolymerItemInteraction(ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, InteractionResult actionResult) {
        return true;
    }

    @Override
    public boolean isIgnoringItemInteractionPlaySoundExceptedEntity(ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world) {
        return true;
    }
}
