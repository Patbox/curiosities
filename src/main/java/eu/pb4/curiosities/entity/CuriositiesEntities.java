package eu.pb4.curiosities.entity;

import eu.pb4.curiosities.block.*;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.Map;
import java.util.function.Function;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesEntities {
    EntityType<JukeboxMinecart> JUKEBOX_MINECART = register("jukebox_minecart", EntityType.Builder.of(JukeboxMinecart::new, MobCategory.MISC).noLootTable().sized(0.98F, 0.7F).passengerAttachments(0.1875F).clientTrackingRange(8));

    static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        var type = builder.build(ResourceKey.create(Registries.ENTITY_TYPE, id(path)));
        PolymerEntityUtils.registerType(type);
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id(path), type);
    }

    static void init() {
    }
}
