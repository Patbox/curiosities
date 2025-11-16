package eu.pb4.curiosities.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesItemTags {
    TagKey<Item> ELEVATORS = of("elevators");
    TagKey<Item> REPAIRS_SLIME_ARMOR = of("repairs_slime_armor");

    static TagKey<Item> of(String path) {
        return TagKey.create(Registries.ITEM, id(path));
    }

    static void init() {
    }
}
