package eu.pb4.curiosities.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import xyz.nucleoid.packettweaker.PacketContext;

public class InvisiblePressurePlateBlock extends PressurePlateBlock implements PolymerTexturedBlock {
    private static final BlockState STATE = PolymerBlockResourceUtils.requestEmpty(BlockModelType.ACTIVE_PRESSURE_PLATE);

    public InvisiblePressurePlateBlock(Properties properties) {
        super(BlockSetType.STONE, properties);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return STATE;
    }
}
