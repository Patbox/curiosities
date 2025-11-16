package eu.pb4.curiosities.other;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public interface CuriositiesUtils {
    List<DyeColor> COLORS_CREATIVE = List.of(DyeColor.WHITE,
            DyeColor.LIGHT_GRAY,
            DyeColor.GRAY,
            DyeColor.BLACK,
            DyeColor.BROWN,
            DyeColor.RED,
            DyeColor.ORANGE,
            DyeColor.YELLOW,
            DyeColor.LIME,
            DyeColor.GREEN,
            DyeColor.CYAN,
            DyeColor.LIGHT_BLUE,
            DyeColor.BLUE,
            DyeColor.PURPLE,
            DyeColor.MAGENTA,
            DyeColor.PINK);


    static void addOutlineParticles(Level level, BlockPos pos, VoxelShape shape, ParticleOptions particleOptions) {
        addOutlineParticles(level, pos, shape, particleOptions, 0.25);
    }

    static void addOutlineParticles(Level level, BlockPos pos, VoxelShape shape, ParticleOptions particleOptions, double density) {
        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
                    double widthX = Math.min(1.0, x2 - x1);
                    double widthY = Math.min(1.0, y2 - y1);
                    double widthZ = Math.min(1.0, z2 - z1);
                    int countX = Math.max(2, Mth.ceil(widthX / density));
                    int countY = Math.max(2, Mth.ceil(widthY / density));
                    int countZ = Math.max(2, Mth.ceil(widthZ / density));

                    for (int xx = 0; xx < countX; xx++) {
                        for (int yy = 0; yy < countY; yy++) {
                            for (int zz = 0; zz < countZ; zz++) {
                                double relX = (xx + 0.5) / countX;
                                double relY = (yy + 0.5) / countY;
                                double relZ = (zz + 0.5) / countZ;
                                double x = relX * widthX + x1;
                                double y = relY * widthY + y1;
                                double z = relZ * widthZ + z1;
                                ((ServerLevel) level).sendParticles(particleOptions, pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0, relX - 0.5, relY - 0.5, relZ - 0.5, 1);
                            }
                        }
                    }
                }
        );
    }
}
