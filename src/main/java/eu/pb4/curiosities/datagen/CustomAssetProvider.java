package eu.pb4.curiosities.datagen;

import com.google.common.hash.HashCode;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.PalettedPermutationsAtlasSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ConditionItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.SelectItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.CustomModelDataFlagProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.DisplayContextProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
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
                var blockAtlas = AtlasAsset.builder();
                var map = new HashMap<Identifier, ItemAsset>();
                createShulkerPalette(assetWriter);

                createItems(map::put);
                map.forEach((id, asset) -> assetWriter.accept(AssetPaths.itemAsset(id), asset.toJson().getBytes(StandardCharsets.UTF_8)));
                writeBlocksAndItems(assetWriter, blockAtlas);

                assetWriter.accept("assets/minecraft/atlases/blocks.json", blockAtlas.build().toBytes());
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
        consumer.accept(id("cross_rail"), new ItemAsset(new BasicItemModel(id("item/cross_rail"))));

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

    private void writeBlocksAndItems(BiConsumer<String, byte[]> writer, AtlasAsset.Builder blockAtlas) {
        var perm = PalettedPermutationsAtlasSource.builder(id("palette/shulker/base"));
        perm.texture(id("block/elevator_top"));
        perm.texture(id("block/elevator_top_forward"));
        perm.texture(id("block/elevator_top_corner"));
        perm.texture(id("block/elevator_side"));
        for (var color : DyeColor.values()) {
            perm.permutation(color.getSerializedName(), id("palette/shulker/" + color.getSerializedName()));
        }
        blockAtlas.palettedPermutations(perm);


        for (var variant : new String[] {"", "_forward", "_corner"}) {
            writer.accept("assets/curiosities/models/block/elevator" + variant + ".json", ModelAsset.builder()
                    .parent(Identifier.withDefaultNamespace("block/cube_bottom_top"))
                    .texture("top", "curiosities:block/elevator_top" + variant)
                    .texture("side", "curiosities:block/elevator_side")
                    .texture("bottom", "#top")
                    .build().toBytes()
            );

            for (var color : DyeColor.values()) {
                writer.accept("assets/curiosities/models/block/" + color.getSerializedName() + "_elevator" + variant + ".json", ModelAsset.builder()
                        .parent(Identifier.withDefaultNamespace("block/cube_bottom_top"))
                        .texture("top", "curiosities:block/elevator_top" + variant + "_" + color.getSerializedName())
                        .texture("side", "curiosities:block/elevator_side_" + color.getSerializedName())
                        .texture("bottom", "#top")
                        .build().toBytes()
                );
            }
        }
    }

    private void createShulkerPalette(BiConsumer<String, byte[]> assetWriter) throws Exception {
        var jar = PolymerCommonUtils.getClientJarRoot();
        var b = new ByteArrayOutputStream();

        // Palette
        var baseShulker = ImageIO.read(Files.newInputStream(jar.resolve("assets/minecraft/textures/block/shulker_box.png")));
        var positions = new ArrayList<int[]>();

        var existingColors = new IntOpenHashSet();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                var rgb = baseShulker.getRGB(x, y);
                if (existingColors.add(rgb)) {
                    positions.add(new int[]{x, y});
                }
            }
        }

        var palette = new BufferedImage(positions.size(), 1, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < positions.size(); i++) {
            var pos = positions.get(i);
            palette.setRGB(i, 0, baseShulker    .getRGB(pos[0], pos[1]));
        }
        ImageIO.write(palette, "png", b);
        assetWriter.accept("assets/curiosities/textures/palette/shulker/base.png", b.toByteArray());
        b.reset();

        for (var color : DyeColor.values()) {
            var input = ImageIO.read(Files.newInputStream(jar.resolve("assets/minecraft/textures/block/" + color.getSerializedName() + "_shulker_box.png")));
            for (int i = 0; i < positions.size(); i++) {
                var pos = positions.get(i);
                palette.setRGB(i, 0, input.getRGB(pos[0], pos[1]));
            }
            ImageIO.write(palette, "png", b);
            assetWriter.accept("assets/curiosities/textures/palette/shulker/" + color.getSerializedName() + ".png", b.toByteArray());
            b.reset();
        }

    }

    @Override
    public String getName() {
        return "polydecorations:assets";
    }
}
