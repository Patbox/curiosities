package eu.pb4.curiosities.item;

import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesEquipmentAssets {
    ResourceKey<EquipmentAsset> SLIME = of("slime");
    static ResourceKey<EquipmentAsset> of(String path) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, id(path));
    }
}