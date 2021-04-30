package com.syntheticentropy.cogni.forge.symbol;

import net.minecraft.block.Block;

public class TextValue extends BaseValue {
    private final String text;
    public TextValue(String text) {
        super(Type.Text);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public boolean equalsValue(BaseValue b) {
        TextValue ba = this;
        if(!super.equalsValue(b)) return false;
        TextValue bb = (TextValue) b;
        return  ba.text.equals(bb.text);
    }
}
