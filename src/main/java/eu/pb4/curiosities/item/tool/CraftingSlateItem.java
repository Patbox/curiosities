package eu.pb4.curiosities.item.tool;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;

public class CraftingSlateItem extends SimplePolymerItem {
    public CraftingSlateItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action == ClickAction.PRIMARY || !other.isEmpty()) {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetCursorItemPacket(serverPlayer.containerMenu.getCarried()));
            this.use(null, player, null);
        }
        return true;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        player.openMenu(new MenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                return new CraftingMenu(containerId, playerInventory);
            }

            @Override
            public Component getDisplayName() {
                return CraftingSlateItem.this.getName();
            }

            @Override
            public boolean shouldCloseCurrentScreen() {
                return false;
            }
        });
        serverPlayer.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5f, 1f);
        return InteractionResult.SUCCESS_SERVER;
    }
}
