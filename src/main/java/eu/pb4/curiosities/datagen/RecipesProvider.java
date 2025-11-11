package eu.pb4.curiosities.datagen;

import eu.pb4.curiosities.item.CuriositiesItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

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
                this.shaped(RecipeCategory.MISC, CuriositiesItems.PHASER)
                        .pattern("rgr")
                        .pattern("gpg")
                        .pattern("rgr")
                        .define('r', Items.REDSTONE)
                        .define('g', Items.GOLD_INGOT)
                        .define('p', Items.ENDER_PEARL)
                        .unlockedBy("pearl", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENDER_PEARL))
                        .save(recipeOutput);
            }
        };
    }
}
