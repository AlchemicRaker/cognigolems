package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public class SampleSolution extends Solution {
    private String text;
    public SampleSolution() {
        super("Sample");
    }
    public SampleSolution(String text) {
        this();
        this.text = text;
    }

    @Override
    public boolean tick(ICogniEntity entity) {
        System.out.println(this.getName() + ":" + this.getText());
        return true;
    }

    public String getText() {
        return text;
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        super.load(compoundNBT);
        this.text = compoundNBT.contains("text") ? compoundNBT.getString("text") : "N/A";
    }

    @Override
    public boolean save(CompoundNBT compoundNBT) {
        boolean b = super.save(compoundNBT);
        compoundNBT.putString("text", this.text);
        return b;
    }
}
