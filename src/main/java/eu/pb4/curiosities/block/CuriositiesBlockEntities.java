package eu.pb4.curiosities.block;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static eu.pb4.curiosities.ModInit.id;

public class CuriositiesBlockEntities {
    public static final BlockEntityType<PhasingBlockEntity> PHASING_BLOCK = register("phasing_block", FabricBlockEntityTypeBuilder.create(PhasingBlockEntity::new, CuriositiesBlocks.PHASING_BLOCK));

    public static <T extends BlockEntity> BlockEntityType<T> register(String path, FabricBlockEntityTypeBuilder<T> builder) {
        var val = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(path), builder.build());
        PolymerBlockUtils.registerBlockEntity(val);
        return val;
    }

    public static void init() {
    }
}
