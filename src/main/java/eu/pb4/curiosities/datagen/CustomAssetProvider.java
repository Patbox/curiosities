package eu.pb4.curiosities.datagen;

import com.google.common.hash.HashCode;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ConditionItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.SelectItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.CustomModelDataFlagProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.DisplayContextProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static eu.pb4.curiosities.ModInit.id;


record CustomAssetProvider(FabricDataOutput output) implements DataProvider {

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        BiConsumer<String, byte[]> assetWriter = (path, data) -> {
            try {
                cachedOutput.writeIfNeeded(this.output.getOutputFolder().resolve(path), data, HashCode.fromBytes(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        return CompletableFuture.runAsync(() -> {
            try {
                var map = new HashMap<Identifier, ItemAsset>();
                createItems(map::put);
                map.forEach((id, asset) -> assetWriter.accept(AssetPaths.itemAsset(id), asset.toJson().getBytes(StandardCharsets.UTF_8)));
                writeBlocksAndItems(assetWriter);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, Util.backgroundExecutor());


    }

    private void createItems(BiConsumer<Identifier, ItemAsset> consumer) {
        for (var key : BuiltInRegistries.ITEM.keySet()) {
            if (!key.getNamespace().equals("curiosities")) {
                continue;
            }

            var val = BuiltInRegistries.ITEM.getValue(key);
            consumer.accept(key, new ItemAsset(new BasicItemModel(key.withPrefix(val instanceof BlockItem ? "block/" : "item/"))));
        }

        consumer.accept(id("slime_bucket"), new ItemAsset(
                SelectItemModel.builder(new DisplayContextProperty())
                        .withCase(List.of(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND), new ConditionItemModel(
                                new CustomModelDataFlagProperty(0),
                                new BasicItemModel(id("item/slime_bucket_active")),
                                new BasicItemModel(id("item/slime_bucket"))
                        ))
                        .fallback(new BasicItemModel(id("item/slime_bucket")))
                        .build(), new ItemAsset.Properties(false, true)))
        ;
    }

    private void writeBlocksAndItems(BiConsumer<String, byte[]> writer) {
        for (var color : DyeColor.values()) {
            writer.accept("assets/curiosities/models/block/" + color.getSerializedName() + "_elevator.json", ModelAsset.builder()
                    .parent(Identifier.withDefaultNamespace("block/cube_all"))
                    .texture("all", "minecraft:block/" + color.getSerializedName() + "_shulker_box")
                    .build().toBytes()
            );
        }
    }

    @Override
    public String getName() {
        return "polydecorations:assets";
    }
}
