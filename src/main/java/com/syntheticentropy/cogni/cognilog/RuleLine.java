package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Optional;

public class RuleLine<T> extends Line<T> {
    private final List<RuleImplementation> ruleImplementations;
    public RuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<RuleImplementation> ruleImplementations) {
        super(argumentSymbols, argumentTypes);
        this.ruleImplementations = ruleImplementations;
    }

    public List<RuleImplementation> getRuleImplementations() {
        return ruleImplementations;
    }

    public boolean isRule() {
        return true;
    }

}
