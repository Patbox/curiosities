package eu.pb4.curiosities.item;

import eu.pb4.curiosities.block.CuriositiesBlocks;
import eu.pb4.curiosities.block.PhasingBlock;
import eu.pb4.curiosities.block.PhasingBlockEntity;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.phys.shapes.Shapes;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.Consumer;

public class PhaserItem extends Item implements PolymerItem {
    public PhaserItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var target = context.getLevel().getBlockState(context.getClickedPos());
        var speed = target.getDestroySpeed(context.getLevel(), context.getClickedPos());
        if (target.hasBlockEntity() || !target.getShape(context.getLevel(), context.getClickedPos()).equals(Shapes.block())
                || target.getBlock() instanceof GameMasterBlock || speed > 25.0F || speed < 0) {
            return InteractionResult.FAIL;
        }

        context.getLevel().setBlockAndUpdate(context.getClickedPos(),
                CuriositiesBlocks.PHASING_BLOCK.defaultBlockState().setValue(PhasingBlock.LIGHT, target.getLightEmission()));
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof PhasingBlockEntity be) {
            be.setVisualState(target);
        }

        context.getItemInHand().consume(1, context.getPlayer());

        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable("item.curiosities.phaser.desc.1").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        tooltipAdder.accept(Component.translatable("item.curiosities.phaser.desc.2").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        tooltipAdder.accept(Component.translatable("item.curiosities.phaser.desc.3").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }
}
