package com.syntheticentropy.cogni.forge.symbol;

import com.syntheticentropy.cogni.cognilog.Symbol;
import net.minecraft.block.Blocks;

import java.util.Optional;

public abstract class BaseValue {
    public enum Type {
        Coordinate,
        BlockType,
        ItemType,
        Text
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

    public boolean isBlockType() {
        return this.typeIndex == Type.BlockType.ordinal();
    }

    public BlockTypeValue getBlockTypeValue() {
        return (BlockTypeValue) this;
    }

    public boolean equalsValue(BaseValue b) {
        BaseValue a = this;
        return a.typeIndex == b.typeIndex;
    }
}
