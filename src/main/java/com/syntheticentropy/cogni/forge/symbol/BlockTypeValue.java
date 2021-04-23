package com.syntheticentropy.cogni.forge.symbol;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public class BlockTypeValue extends BaseValue {
    private final Block blockType;
    public BlockTypeValue(Block blockType) {
        super(Type.BlockType);
        this.blockType = blockType;
    }

    public Block getBlockType() {
        return blockType;
    }
}
