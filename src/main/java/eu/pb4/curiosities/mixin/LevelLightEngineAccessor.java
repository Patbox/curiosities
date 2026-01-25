package eu.pb4.curiosities.mixin;

import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.lighting.LevelLightEngine.class)
public interface LevelLightEngineAccessor {
    @Accessor
    LightEngine<?, ?> getBlockEngine();
}
