package com.syntheticentropy.cogni.forge.symbol;

import com.syntheticentropy.cogni.cognilog.Symbol;

import java.util.Optional;

public abstract class BaseValue {
    public enum Type {
        Coordinate
    }
    private final int typeIndex;
    public BaseValue(Type type) {
        this.typeIndex = type.ordinal();
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    public boolean isCoordinate() {
        return this.typeIndex == Type.Coordinate.ordinal();
    }

    public CoordinateValue getCoordinateValue() {
        return (CoordinateValue) this;
    }
}
