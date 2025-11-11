package eu.pb4.curiosities;

import eu.pb4.curiosities.block.CuriositiesBlockEntities;
import eu.pb4.curiosities.item.CuriositiesDataComponents;
import eu.pb4.curiosities.other.CuriositiesSoundEvents;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.curiosities.item.CuriositiesItems;
import eu.pb4.curiosities.block.CuriositiesBlocks;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModInit implements ModInitializer {
	public static final String MOD_ID = "curiosities";


	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CuriositiesSoundEvents.init();
		CuriositiesDataComponents.init();
		CuriositiesBlocks.init();
		CuriositiesItems.init();
		CuriositiesBlockEntities.init();

		PolymerResourcePackUtils.addModAssets(MOD_ID);
		//ResourcePackExtras.forDefault().addBridgedModelsFolder(id("block"));
	}

	public static final Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}