package eu.pb4.curiosities.other;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.Set;

public enum OptionalDirection8 implements StringRepresentable {
    NONE("none"),
    NORTH("north", Direction.NORTH),
    NORTH_EAST("north_east", Direction.NORTH, Direction.EAST),
    EAST("east", Direction.EAST),
    SOUTH_EAST("south_east", Direction.EAST, Direction.SOUTH),
    SOUTH("south", Direction.SOUTH),
    SOUTH_WEST("south_west", Direction.SOUTH, Direction.WEST),
    WEST("west", Direction.WEST),
    NORTH_WEST("north_west", Direction.WEST, Direction.NORTH);

    private final Direction[] directions;
    private final BlockPos.MutableBlockPos step;
    private final String name;

    private final OptionalDouble yaw;

    OptionalDirection8(String name, final Direction... directions) {
        this.name = name;
        this.directions = directions;
        this.step = new BlockPos.MutableBlockPos();

        if (directions.length == 0) {
            this.yaw = OptionalDouble.empty();
        } else if (directions.length == 1) {
            this.yaw = OptionalDouble.of(directions[0].toYRot());
            this.step.set(directions[0].getUnitVec3i());
        } else {
            for (var dir : directions) {
                this.step.setX(this.step.getX() + dir.getStepX()).setY(this.step.getY() + dir.getStepY()).setZ(this.step.getZ() + dir.getStepZ());
            }

            this.yaw = OptionalDouble.of(directions[0].toYRot() + Mth.degreesDifference(directions[0].toYRot(), directions[1].toYRot()) / 2);
        }
    }

    public int getStepX() {
        return this.step.getX();
    }

    public int getStepZ() {
        return this.step.getZ();
    }

    public OptionalDouble yaw() {
        return yaw;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public float getModelYaw() {
        if (this == NONE) {
            return 0;
        }
        return this.directions[0].toYRot();
    }

    public boolean isCorner() {
        return this.directions.length == 2;
    }

    public boolean isForward() {
        return this.directions.length == 1;
    }
}
