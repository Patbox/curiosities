package eu.pb4.curiosities.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.Map;
import java.util.function.Function;

import static eu.pb4.curiosities.ModInit.id;

public interface CuriositiesBlockTags {
    TagKey<Block> ELEVATORS = of("elevators");
    TagKey<Block> UNPHASEABLE = of("unphaseable");

    static TagKey<Block> of(String path) {
        return TagKey.create(Registries.BLOCK, id(path));
    }

    static void init() {
    }
}
