package eu.pb4.curiosities.datagen;


import eu.pb4.curiosities.block.CuriositiesBlocks;
import eu.pb4.curiosities.item.CuriositiesItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.concurrent.CompletableFuture;

class LootTables extends FabricBlockLootTableProvider {

    protected LootTables(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        this.dropOther(CuriositiesBlocks.PHASING_BLOCK, CuriositiesItems.PHASER);
        this.dropSelf(CuriositiesBlocks.ANGEL_BLOCK);
        this.dropSelf(CuriositiesBlocks.INVISIBLE_PRESSURE_PLATE);
        this.dropSelf(CuriositiesBlocks.CROSS_RAIL);
        this.dropSelf(CuriositiesBlocks.ELEVATOR);
        CuriositiesBlocks.COLORED_ELEVATOR.values().forEach(this::dropSelf);
    }
}
