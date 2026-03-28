package eu.pb4.curiosities.datagen;


import eu.pb4.curiosities.block.CuriositiesBlockTags;
import eu.pb4.curiosities.item.CuriositiesItemTags;
import eu.pb4.curiosities.item.CuriositiesItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class ItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {


    public ItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture, @Nullable FabricTagsProvider.BlockTagsProvider blockTagsProvider) {
        super(output, registriesFuture, blockTagsProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.copy(CuriositiesBlockTags.ELEVATORS, CuriositiesItemTags.ELEVATORS);
        this.valueLookupBuilder(CuriositiesItemTags.REPAIRS_SLIME_ARMOR)
                .add(Items.SLIME_BALL);

        this.valueLookupBuilder(ItemTags.FOOT_ARMOR)
                .add(CuriositiesItems.SLIME_BOOTS);

        this.valueLookupBuilder(ItemTags.HEAD_ARMOR)
                .add(CuriositiesItems.MINING_HELMET);
    }
}
