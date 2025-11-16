package eu.pb4.curiosities.item;

import eu.pb4.curiosities.block.CuriositiesBlocks;
import eu.pb4.curiosities.item.block.AngelBlockItem;
import eu.pb4.curiosities.item.block.PhaserItem;
import eu.pb4.curiosities.item.tool.CraftingSlateItem;
import eu.pb4.curiosities.item.tool.SlimeBucketItem;
import eu.pb4.curiosities.other.CuriositiesUtils;
import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
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
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesItems {
    Item PHASER = register("phaser", PhaserItem::new);
    Item CRAFTING_SLATE = register("crafting_slate",  new Item.Properties().stacksTo(1), CraftingSlateItem::new);
    Item SLIME_BOOTS = register("slime_boots",  new Item.Properties().humanoidArmor(CuriositiesArmorMaterials.SLIME, ArmorType.BOOTS), SimplePolymerItem::new);

    Item SLIME_BUCKET = register("slime_bucket", new Item.Properties().stacksTo(1).component(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY),
            (properties) -> new SlimeBucketItem(EntityType.SLIME, properties));

    Item INVISIBLE_PRESSURE_PLATE = register(CuriositiesBlocks.INVISIBLE_PRESSURE_PLATE, (block, properties) -> new FactoryBlockItem(block, properties.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    Item CROSS_RAIL = register(CuriositiesBlocks.CROSS_RAIL);
    Item ANGEL_BLOCK = register(CuriositiesBlocks.ANGEL_BLOCK, AngelBlockItem::new);
    Item ELEVATOR = register(CuriositiesBlocks.ELEVATOR);
    Map<DyeColor, Item> COLORED_ELEVATOR = Util.makeEnumMap(DyeColor.class, color -> register(CuriositiesBlocks.COLORED_ELEVATOR.get(color)));


    static <T extends Block & PolymerBlock> BlockItem register(T block) {
        return register(block, FactoryBlockItem::new);
    }

    static <T extends Block & PolymerBlock> BlockItem register(T block, BiFunction<T, Item.Properties, BlockItem> consumer) {
        return register(BuiltInRegistries.BLOCK.getKey(block).getPath(), new Item.Properties().useBlockDescriptionPrefix(), x -> consumer.apply(block, x));
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
                    output.accept(CRAFTING_SLATE);
                    //output.accept(SLIME_BOOTS);
                    output.accept(ANGEL_BLOCK);
                    output.accept(INVISIBLE_PRESSURE_PLATE);
                    output.accept(CROSS_RAIL);
                    output.accept(ELEVATOR);

                    for (var color : CuriositiesUtils.COLORS_CREATIVE) {
                        output.accept(COLORED_ELEVATOR.get(color));
                    }
                }).build()
        );
    }
}