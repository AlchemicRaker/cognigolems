package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

public abstract class Solution {

    private String name;
    public Solution(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Return true to mark this solution as completed
    // solutions are responsible for making sure their tick() code is fast!
    public abstract boolean tick(ICogniEntity entity);

    public void load(CompoundNBT compoundNBT) {
        this.name = compoundNBT.contains("name") ? compoundNBT.getString("name") : "N/A";
    }

    public boolean save(CompoundNBT compoundNBT) {
        compoundNBT.putString("name", this.name);
        return true;
    }

    public static Solution fromNBT(CompoundNBT compoundNBT) {
        String name = compoundNBT.contains("name") ? compoundNBT.getString("name") : null;
        if(name == null) return null;
        Solution solution = null;
        if(name.equals("Sample")) {
            solution = new SampleSolution();
        }else if(name.equals("moveToCoord")) {
            solution = new MoveToCoordSolution();
        }
        if(solution != null) {
            solution.load(compoundNBT);
            return solution;
        }
        return null;
    }

    public static CompoundNBT toNBT(Solution solution) {
        CompoundNBT compoundNBT = new CompoundNBT();
        solution.save(compoundNBT);
        return compoundNBT;
    }
}
