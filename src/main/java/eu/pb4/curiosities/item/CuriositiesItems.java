package eu.pb4.curiosities.item;

import eu.pb4.curiosities.block.CuriositiesBlocks;
import eu.pb4.curiosities.other.CuriositiesUtils;
import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Function;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesItems {
    Item PHASER = register("phaser", PhaserItem::new);

    Item SLIME_BUCKET = register("slime_bucket", new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY),
            (properties) -> new SlimeBucketItem(EntityType.SLIME, properties));

    Map<DyeColor, Item> ELEVATOR = Util.makeEnumMap(DyeColor.class, color -> register(CuriositiesBlocks.ELEVATOR.get(color)));


    static BlockItem register(Block block) {
        return register(BuiltInRegistries.BLOCK.getKey(block).getPath(), new Item.Properties().useBlockDescriptionPrefix(), x -> new FactoryBlockItem((Block & PolymerBlock) block, x));
    }

    static <T extends Item> T register(String path, Function<Item.Properties, T> function) {
        return register(path, new Item.Properties(), function);
    }

    static <T extends Item> T register(String path, Item.Properties properties, Function<Item.Properties, T> function) {
        return Registry.register(BuiltInRegistries.ITEM, id(path), function.apply(properties.setId(ResourceKey.create(Registries.ITEM, id(path)))));
    }

    static void init() {
        PolymerItemGroupUtils.registerPolymerItemGroup(id("main"), PolymerItemGroupUtils.builder()
                .icon(CuriositiesItems.PHASER::getDefaultInstance)
                .title(Component.literal("Curiosities"))
                .displayItems((parameters, output) -> {
                    output.accept(PHASER);
                    output.accept(SLIME_BUCKET);

                    for (var color : CuriositiesUtils.COLORS_CREATIVE) {
                        output.accept(ELEVATOR.get(color));
                    }
                }).build()
        );
    }
}