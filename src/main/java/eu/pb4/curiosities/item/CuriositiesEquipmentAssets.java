package eu.pb4.curiosities.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesEquipmentAssets {
    ResourceKey<EquipmentAsset> SLIME = of("slime");
    ResourceKey<EquipmentAsset> MINING = of("mining");
    static ResourceKey<EquipmentAsset> of(String path) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, id(path));
    }
}