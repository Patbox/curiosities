package eu.pb4.curiosities.datagen;


import eu.pb4.curiosities.block.CuriositiesBlocks;
import eu.pb4.curiosities.item.CuriositiesItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

class LootTables extends FabricBlockLootSubProvider {

    protected LootTables(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
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
