package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import com.syntheticentropy.cogni.forge.entity.goal.MoveToGoal;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.util.math.vector.Vector3d;

public class MoveToCoordSolution extends Solution {
    private final double x;
    private final double y;
    private final double z;
    private final double range;
    private boolean started;

    public MoveToCoordSolution(CoordinateValue coordinateValue) {
        super("moveToCoord");
        this.x = coordinateValue.getX();
        this.y = coordinateValue.getY();
        this.z = coordinateValue.getZ();
        this.range = 1;
        this.started = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getRange() {
        return range;
    }

    @Override
    public boolean tick(ICogniEntity entity) {
        if(!entity.getMoveToGoal().isPresent() || !entity.getCreatureEntity().isPresent()) return true; // Can't move
        MoveToGoal moveToGoal = entity.getMoveToGoal().get();

        if(started) {
            return moveToGoal.isDone();
        }

        started = true;

        moveToGoal.activate(this.x, this.y, this.z, this.range);
        return false;
    }
}
