package eu.pb4.curiosities.datagen;


import eu.pb4.curiosities.block.CuriositiesBlockTags;
import eu.pb4.curiosities.item.CuriositiesItemTags;
import eu.pb4.curiosities.item.CuriositiesItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

class EntityTagsProvider extends FabricTagProvider.EntityTypeTagProvider {


    public EntityTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
