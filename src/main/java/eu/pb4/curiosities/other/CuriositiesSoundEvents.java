package eu.pb4.curiosities.other;

import eu.pb4.polymer.core.api.other.PolymerSoundEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesSoundEvents {
    SoundEvent BLOCK_PHASING_POP_OUT = register("block.phasing.pop_out");
    SoundEvent BLOCK_PHASING_POP_IN = register("block.phasing.pop_in");
    SoundEvent BLOCK_ELEVATOR_MODIFY = register("block.elevator.modify");
    SoundEvent ITEM_BUCKET_SLIME_FILL = register("item.bucket.slime.fill");
    SoundEvent ITEM_BUCKET_SLIME_EMPTY = register("item.bucket.slime.empty");


    static SoundEvent register(String path) {
        return register(path, new SoundEvent(id(path), Optional.empty()));
    }

    static SoundEvent register(String path, SoundEvent event) {
        Registry.register(BuiltInRegistries.SOUND_EVENT, id(path), event);
        PolymerSoundEvent.registerOverlay(event);
        return event;
    }

    static void init() {}
}
