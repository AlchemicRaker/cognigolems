package com.syntheticentropy.cogni.forge.symbol;

import net.minecraft.util.Direction;

public class CoordinateValue extends BaseValue {
    private final double x;
    private final double y;
    private final double z;
    private final Direction side;
    private final Integer slot;
//    private final Integer entityIdentifier;

    public CoordinateValue(double x, double y, double z, Direction side, Integer slot) {
        super(Type.Coordinate);
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.slot = slot;
    }

    public CoordinateValue(double x, double y, double z, Direction side) {
        this(x, y, z, side, null);
    }

    public CoordinateValue(double x, double y, double z) {
        this(x, y, z, null);
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

    public Direction getSide() {
        return side;
    }

    public Integer getSlot() {
        return slot;
    }

    public boolean equalsValue(BaseValue b) {
        CoordinateValue ca = this;
        if(!super.equalsValue(b)) return false;
        CoordinateValue cb = (CoordinateValue) b;
        return  ca.x == cb.x &&
                ca.y == cb.y &&
                ca.z == cb.z &&
                ca.side == cb.side &&
                ((ca.slot == null && cb.slot == null) ||
                 (ca.slot != null && cb.slot != null &&
                         ca.slot.equals(cb.slot)));
    }
}