package com.syntheticentropy.cogni.forge.symbol;

public class CoordinateValue extends BaseValue {
    private final double x;
    private final double y;
    private final double z;

    public CoordinateValue(double x, double y, double z) {
        super(Type.Coordinate);
        this.x = x;
        this.y = y;
        this.z = z;
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
}
