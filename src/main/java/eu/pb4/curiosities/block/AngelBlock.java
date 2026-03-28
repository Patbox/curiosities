package eu.pb4.curiosities.block;


import eu.pb4.factorytools.api.block.CustomBreakingParticleBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public class AngelBlock extends Block implements FactoryBlock, CustomBreakingParticleBlock {
    private ParticleOptions breakingParticle;
    public AngelBlock(Properties properties) {
        super(properties);

        var p = DataComponentPatch.builder();
        p.set(DataComponents.ITEM_MODEL, ResourcePackExtras.bridgeModel(properties.blockIdOrThrow().identifier().withPrefix("block/")));
        this.breakingParticle = new ItemParticleOption(ParticleTypes.ITEM, new ItemStackTemplate(Items.STONE, p.build()));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        getDrops(state, (ServerLevel) level, pos, blockEntity, player, stack).forEach((stackx) -> {
            stackx = stackx.copy();
            if (!player.addItem(stackx) && !stackx.isEmpty()) {
                popResource(level, pos, stackx);
            }
        });
    }


    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return new Model(initialBlockState);
    }

    @Override
    public ParticleOptions getBreakingParticle(BlockState blockState) {
        return this.breakingParticle;
    }

    public static final class Model extends BlockModel {
        public Model(BlockState state) {
            var base = ItemDisplayElementUtil.createSimple(state.getBlock().asItem());
            base.setScale(new Vector3f(2f));
            this.addElement(base);
        }
    }
}
