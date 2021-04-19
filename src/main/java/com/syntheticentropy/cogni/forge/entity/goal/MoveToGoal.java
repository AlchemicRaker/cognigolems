package com.syntheticentropy.cogni.forge.entity.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MoveToGoal extends Goal {
    protected final CreatureEntity mob;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;
    protected double range;
    protected boolean readyToUse;

    public MoveToGoal(CreatureEntity mob) {
        this.mob = mob;
        this.readyToUse = false;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public void activate(double x, double y, double z, double range) {
        this.wantedX = x;
        this.wantedY = y;
        this.wantedZ = z;
        this.range = range;
        this.readyToUse = true;
    }

    @Override
    public boolean canUse() {
        return this.readyToUse;
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone();
    }

    public boolean isDone() {
        return this.mob.getNavigation().isDone();
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, 1.0);
    }

    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }
}
