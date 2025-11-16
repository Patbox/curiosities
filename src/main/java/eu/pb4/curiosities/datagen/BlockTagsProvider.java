package eu.pb4.curiosities.datagen;

import eu.pb4.curiosities.block.CuriositiesBlockTags;
import eu.pb4.curiosities.block.CuriositiesBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;


class BlockTagsProvider extends FabricTagProvider.BlockTagProvider {

    public BlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //noinspection unchecked
        this.valueLookupBuilder(CuriositiesBlockTags.ELEVATORS)
                .add(CuriositiesBlocks.ELEVATOR)
                .addAll((Collection<Block>) (Object) CuriositiesBlocks.COLORED_ELEVATOR.values());

        this.valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .addOptionalTag(CuriositiesBlockTags.ELEVATORS)
                .add(CuriositiesBlocks.INVISIBLE_PRESSURE_PLATE)
                .add(CuriositiesBlocks.CROSS_RAIL)
        ;

        this.valueLookupBuilder(CuriositiesBlockTags.UNPHASEABLE)
                .addOptionalTag(CuriositiesBlockTags.ELEVATORS)
                .addOptionalTag(ConventionalBlockTags.OBSIDIANS)
                .addOptionalTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
                .add(Blocks.BEDROCK)
                .add(Blocks.BARRIER)
                .add(Blocks.END_PORTAL)
                .add(Blocks.NETHER_PORTAL)
                .add(Blocks.END_PORTAL_FRAME);

        this.valueLookupBuilder(BlockTags.RAILS)
                .add(CuriositiesBlocks.CROSS_RAIL);
    }
}
