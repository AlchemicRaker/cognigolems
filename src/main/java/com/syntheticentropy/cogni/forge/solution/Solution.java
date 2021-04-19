package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;

public abstract class Solution {
    // TODO: add de/serialization
    // Solutions will be able to persist through NBT strings

    private final String name;
    public Solution(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Return true to mark this solution as completed
    // solutions are responsible for making sure their tick() code is fast!
    public abstract boolean tick(ICogniEntity entity);
}
