package eu.pb4.curiosities.item;

import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;

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