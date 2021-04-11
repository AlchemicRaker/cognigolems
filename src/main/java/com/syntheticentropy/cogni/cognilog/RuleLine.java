package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Optional;

public class RuleLine extends Line{
    private final List<Object> ruleImplementations;
    public RuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<Object> ruleImplementations) {
        super(argumentSymbols, argumentTypes);
        this.ruleImplementations = ruleImplementations;
    }

    public List<Object> getRuleImplementations() {
        return ruleImplementations;
    }

    public boolean isRule() {
        return true;
    }
}
