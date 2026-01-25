package eu.pb4.curiosities.other;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface EntityLightEngine {
    void curiosities$setLightLevel(BlockPos pos, int level);
    boolean curiosities$hasLightUpdates(long pos);
}
