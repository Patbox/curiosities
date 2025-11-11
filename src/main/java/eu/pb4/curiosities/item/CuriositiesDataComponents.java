package eu.pb4.curiosities.item;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Function;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesDataComponents {
    DataComponentType<Unit> SLIME_ACTIVE = register("slime_active", DataComponentType.<Unit>builder().persistent(Unit.CODEC));

    static <T> DataComponentType<T> register(String path, DataComponentType.Builder<T> function) {
        var val = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id(path), function.build());
        PolymerComponent.registerDataComponent(val);
        return val;
    }

    static void init() {

    }
}