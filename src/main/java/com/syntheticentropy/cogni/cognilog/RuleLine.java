package com.syntheticentropy.cogni.cognilog;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RuleLine extends Line {
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

    public abstract static class RuleImplementation {
        public abstract int complexity();
        public abstract List<Integer> requiredArgumentIndexes();
        
        public Optional<List<Integer>> requiredArgumentSymbols(RuleLine ruleLine) {
            List<Optional<Integer>> argumentSymbols = ruleLine.getArgumentSymbols();
            List<Optional<Integer>> dereferencedArgumentSymbols = requiredArgumentIndexes().stream()
                    .map(argumentSymbols::get)
                    .collect(Collectors.toList());

            if(!dereferencedArgumentSymbols.stream().allMatch(Optional::isPresent)) {
                return Optional.empty();
            }

            return Optional.of(dereferencedArgumentSymbols.stream().map(Optional::get).collect(Collectors.toList()));
        }

        public String toDependencyString(RuleLine ruleLine) {
            return  "RI[$" + complexity() + "] " +
                    requiredArgumentSymbols(ruleLine).orElse(Collections.emptyList()) +
                    " -> " +
                    ruleLine.getArgumentSymbols().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        }
    }
}
