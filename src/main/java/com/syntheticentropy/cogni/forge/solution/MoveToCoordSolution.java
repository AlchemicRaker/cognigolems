package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import com.syntheticentropy.cogni.forge.entity.goal.MoveToGoal;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

public class MoveToCoordSolution extends Solution {
    private double x;
    private double y;
    private double z;
    private double range;
    private boolean started;

    public MoveToCoordSolution() {
        super("moveToCoord");
    }

    public MoveToCoordSolution(CoordinateValue coordinateValue) {
        this();
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

    @Override
    public void load(CompoundNBT compoundNBT) {
        super.load(compoundNBT);
        this.x = compoundNBT.contains("x") ? compoundNBT.getDouble("x") : 0;
        this.y = compoundNBT.contains("y") ? compoundNBT.getDouble("y") : 0;
        this.z = compoundNBT.contains("z") ? compoundNBT.getDouble("z") : 0;
        this.range = compoundNBT.contains("r") ? compoundNBT.getDouble("r") : 0;
        this.started = compoundNBT.contains("s") && compoundNBT.getBoolean("s");
        this.started = false;
    }

    @Override
    public boolean save(CompoundNBT compoundNBT) {
        boolean b = super.save(compoundNBT);
        compoundNBT.putDouble("x", this.x);
        compoundNBT.putDouble("y", this.y);
        compoundNBT.putDouble("z", this.z);
        compoundNBT.putDouble("r", this.range);
        compoundNBT.putBoolean("s", this.started);
        return b;
    }
}
