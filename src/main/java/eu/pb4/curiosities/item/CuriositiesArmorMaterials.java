package eu.pb4.curiosities.item;

import com.google.common.collect.Maps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.Map;

public interface CuriositiesArmorMaterials {
    ArmorMaterial SLIME = new ArmorMaterial(
            5, makeDefense(1, 1, 1, 1, 1), 2, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, -0.5F, CuriositiesItemTags.REPAIRS_SLIME_ARMOR, CuriositiesEquipmentAssets.SLIME
    );

    ArmorMaterial MINING = new ArmorMaterial(
            11, makeDefense(1, 3, 4, 2, 4), 8, SoundEvents.ARMOR_EQUIP_COPPER, 0.0F, 0.0F, ItemTags.REPAIRS_COPPER_ARMOR, CuriositiesEquipmentAssets.MINING);

    private static Map<ArmorType, Integer> makeDefense(int boots, int leggings, int chestplate, int helmet, int body) {
        return Maps.newEnumMap(
                Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, leggings, ArmorType.CHESTPLATE, chestplate, ArmorType.HELMET, helmet, ArmorType.BODY, body)
        );
    }
}