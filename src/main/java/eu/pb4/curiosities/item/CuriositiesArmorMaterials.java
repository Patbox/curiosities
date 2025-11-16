package eu.pb4.curiosities.item;

import com.google.common.collect.Maps;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesArmorMaterials {
    ArmorMaterial SLIME = new ArmorMaterial(
            4, makeDefense(1, 1, 1, 1, 1), 2, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, -0.5F, CuriositiesItemTags.REPAIRS_SLIME_ARMOR, CuriositiesEquipmentAssets.SLIME
    );

    private static Map<ArmorType, Integer> makeDefense(int boots, int leggings, int chestplate, int helmet, int body) {
        return Maps.newEnumMap(
                Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, leggings, ArmorType.CHESTPLATE, chestplate, ArmorType.HELMET, helmet, ArmorType.BODY, body)
        );
    }
}