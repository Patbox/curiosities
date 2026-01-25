package eu.pb4.curiosities.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.curiosities.other.EntityLightEngine;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("ALL")
@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin extends LightEngine implements EntityLightEngine {
    @Unique
    private final Long2IntOpenHashMap extraLights = new Long2IntOpenHashMap();

    @Unique
    private final Long2IntOpenHashMap sectionLightCount = new Long2IntOpenHashMap();

    @Unique
    private final Long2LongOpenHashMap sectionLightRemovalTick = new Long2LongOpenHashMap();

    protected BlockLightEngineMixin(LightChunkGetter chunkSource, BlockLightSectionStorage storage) {
        super(chunkSource, storage);
    }

    @ModifyReturnValue(method = "getEmission", at = @At("RETURN"))
    private int withVirtualEmission(int original, @Local(argsOnly = true) long pos) {
        return Math.max(original, this.extraLights.get(pos));
    }

    @Override
    public void curiosities$setLightLevel(BlockPos pos, int level) {
        var sec = SectionPos.asLong(pos);
        if (level == 0) {
            this.extraLights.remove(pos.asLong());
            var x = this.sectionLightCount.get(sec) - 1;
            if (x <= 0) {
                this.sectionLightCount.remove(sec);
                this.sectionLightRemovalTick.put(sec, this.chunkSource.getLevel() instanceof ServerLevel level1 ? level1.getGameTime() + 5 : 0);
            }
        } else {
            this.extraLights.put(pos.asLong(), level);
            this.sectionLightCount.put(sec, this.sectionLightCount.get(sec) + 1);
        }
    }

    @Override
    public boolean curiosities$hasLightUpdates(long pos) {
        try {
            var removal = this.sectionLightRemovalTick.get(pos);
            if (removal < (this.chunkSource.getLevel() instanceof ServerLevel level1 ? level1.getGameTime() : 1)) {
                this.sectionLightRemovalTick.remove(pos);
                removal = 0;
            }

            return this.sectionLightCount.get(pos) != 0 || removal != 0;
        } catch (Throwable e) {
            return true;
        }
    }
}
