package eu.pb4.curiosities.block;

import eu.pb4.factorytools.api.block.BlockEntityExtraListener;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class PhasingBlockEntity extends BlockEntity implements BlockEntityExtraListener {
    private BlockState visualState = Blocks.STONE.defaultBlockState();
    @Nullable
    private PhasingBlock.Model model;

    public PhasingBlockEntity(BlockPos pos, BlockState blockState) {
        super(CuriositiesBlockEntities.PHASING_BLOCK, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("visual_state", BlockState.CODEC, this.visualState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.visualState = input.read("visual_state", BlockState.CODEC).orElse(Blocks.STONE.defaultBlockState());
        if (this.model != null) {
            this.model.setVisualState(this.visualState);
        }
    }

    @Override
    public void onListenerUpdate(LevelChunk levelChunk) {
        var att = BlockBoundAttachment.get(levelChunk, this.worldPosition);
        if (att != null && att.holder() instanceof PhasingBlock.Model model) {
            this.model = model;
            this.model.setVisualState(this.visualState);
        }
    }

    public BlockState getVisualState() {
        return this.visualState;
    }

    public void setVisualState(BlockState state) {
        this.visualState = state;
        if (this.model != null) {
            this.model.setVisualState(state);
        }
    }
}
