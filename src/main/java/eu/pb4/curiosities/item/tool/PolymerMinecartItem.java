package eu.pb4.curiosities.item.tool;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import xyz.nucleoid.packettweaker.PacketContext;

public class PolymerMinecartItem extends MinecartItem implements PolymerItem {
    public PolymerMinecartItem(EntityType<? extends AbstractMinecart> type, Properties properties) {
        super(type, properties);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.CHEST_MINECART;
    }
}
