package eu.pb4.curiosities.datagen;

import eu.pb4.curiosities.item.CuriositiesItemTags;
import eu.pb4.curiosities.item.CuriositiesItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.TransmuteRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;


class RecipesProvider extends FabricRecipeProvider {


    public RecipesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return "recipes";
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        return new RecipeProvider(provider, recipeOutput) {
            @Override
            public void buildRecipes() {
                this.shaped(RecipeCategory.MISC, CuriositiesItems.PHASER, 2)
                        .pattern("rgr")
                        .pattern("gpg")
                        .pattern("rgr")
                        .define('r', Items.REDSTONE)
                        .define('g', Items.GOLD_INGOT)
                        .define('p', Items.ENDER_PEARL)
                        .unlockedBy("pearl", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENDER_PEARL))
                        .save(recipeOutput);

                this.shaped(RecipeCategory.MISC, CuriositiesItems.ANGEL_BLOCK)
                        .pattern("fgf")
                        .pattern("g g")
                        .pattern("fgf")
                        .define('g', Items.GOLD_INGOT)
                        .define('f', Items.FEATHER)
                        .unlockedBy("pearl", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GOLD_INGOT))
                        .save(recipeOutput);


                this.shaped(RecipeCategory.MISC, CuriositiesItems.INVISIBLE_PRESSURE_PLATE)
                        .pattern("rr")
                        .pattern("##")
                        .define('r', Items.REDSTONE)
                        .define('#', Items.TINTED_GLASS)
                        .unlockedBy("pearl", InventoryChangeTrigger.TriggerInstance.hasItems(Items.TINTED_GLASS))
                        .save(recipeOutput);

                this.shaped(RecipeCategory.MISC, CuriositiesItems.CRAFTING_SLATE)
                        .pattern(" si")
                        .pattern(" cs")
                        .pattern("s  ")
                        .define('s', Items.STICK)
                        .define('c', Items.CRAFTING_TABLE)
                        .define('i', Items.IRON_INGOT)
                        .unlockedBy("pearl", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CRAFTING_TABLE))
                        .save(recipeOutput);

                this.shapeless(RecipeCategory.MISC, CuriositiesItems.CROSS_RAIL)
                        .requires(Items.RAIL, 2)
                        .requires(Items.IRON_INGOT)
                        .unlockedBy("pearl", InventoryChangeTrigger.TriggerInstance.hasItems(Items.RAIL))
                        .save(recipeOutput);


                {
                    this.shaped(RecipeCategory.MISC, CuriositiesItems.ELEVATOR)
                            .pattern("isi")
                            .pattern("ipi")
                            .pattern("isi")
                            .define('i', Items.IRON_INGOT)
                            .define('s', Items.SHULKER_SHELL)
                            .define('p', Items.ENDER_PEARL)
                            .unlockedBy("pearl", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENDER_PEARL))
                            .save(recipeOutput);

                    var elevators = this.tag(CuriositiesItemTags.ELEVATORS);
                    for (var dye : DyeColor.values()) {
                        var dyeItem = DyeItem.byColor(dye);
                        TransmuteRecipeBuilder.transmute(RecipeCategory.MISC, elevators, Ingredient.of(dyeItem), CuriositiesItems.COLORED_ELEVATOR.get(dye)).group("elevator_dye").unlockedBy(getHasName(DyeItem.byColor(dye)), this.has(dyeItem)).save(this.output);
                    }
                }
            }
        };
    }
}
