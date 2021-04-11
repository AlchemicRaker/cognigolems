package com.syntheticentropy.cogni.cognilog;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConstantRuleLine extends RuleLine {

    /*
        Every argument has a single, known value
        All arguments are outputs
        No arguments are inputs
     */

    public ConstantRuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<Object> values) {
        super(argumentSymbols, argumentTypes, Collections.singletonList(new ConstantRuleImplementation(values)));
    }

    public ConstantRuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<Object> values, int complexity) {
        super(argumentSymbols, argumentTypes, Collections.singletonList(new ConstantRuleImplementation(values, complexity)));
    }

    public static class ConstantRuleImplementation extends RuleLine.RuleImplementation {

        // used later in the Iterable implementation, as the only values for each argument
        private final List<Object> values;
        private final int complexity;

        public ConstantRuleImplementation(List<Object> values) {
            this.values = values;
            this.complexity = 0;
        }

        public ConstantRuleImplementation(List<Object> values, int complexity) {
            this.values = values;
            this.complexity = complexity;
        }

        @Override
        public int complexity() {
            return complexity;
        }

        @Override
        public List<Integer> requiredArgumentIndexes() {
            return Collections.emptyList();
        }
    }
}
