package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Optional;

public class RuleLine<T> extends Line<T> {
    private final List<RuleImplementation<T>> ruleImplementations;
    public RuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<RuleImplementation<T>> ruleImplementations) {
        super(argumentSymbols, argumentTypes);
        this.ruleImplementations = ruleImplementations;
    }

    public List<RuleImplementation<T>> getRuleImplementations() {
        return ruleImplementations;
    }

    public boolean isRule() {
        return true;
    }

}
