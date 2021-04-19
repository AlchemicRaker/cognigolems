package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;

public class SampleSolution extends Solution {
    private final String text;
    public SampleSolution(String text) {
        super("Sample");
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
}
