package eu.pb4.curiosities.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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

public interface CuriositiesBlocks {
    PhasingBlock PHASING_BLOCK = register("phasing_block", BlockBehaviour.Properties.of().strength(5).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.BLOCK), PhasingBlock::new);

    AngelBlock ANGEL_BLOCK = register("angel_block", BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOL).noOcclusion(), AngelBlock::new);

    InvisiblePressurePlateBlock INVISIBLE_PRESSURE_PLATE = register("invisible_pressure_plate", BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_PRESSURE_PLATE), InvisiblePressurePlateBlock::new);
    CrossRailBlock CROSS_RAIL = register("cross_rail", BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL), CrossRailBlock::new);
    ElevatorBlock ELEVATOR = register("elevator", BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion(), ElevatorBlock::new);
    Map<DyeColor, ElevatorBlock> COLORED_ELEVATOR = Util.makeEnumMap(DyeColor.class,
            color -> register(color.getSerializedName() + "_elevator", BlockBehaviour.Properties.ofFullCopy(ELEVATOR), ElevatorBlock::new));

    static <T extends Block> T register(String path, BlockBehaviour.Properties properties, Function<BlockBehaviour.Properties, T> function) {
        return Registry.register(BuiltInRegistries.BLOCK, id(path), function.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id(path)))));
    }

    static void init() {
    }
}
