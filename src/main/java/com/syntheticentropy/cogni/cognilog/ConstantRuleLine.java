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

    public static class ConstantRuleImplementation extends RuleLine.RuleImplementation {

        // used later in the Iterable implementation, as the only values for each argument
        private final List<Object> values;

        public ConstantRuleImplementation(List<Object> values) {
            this.values = values;
        }

        @Override
        public int complexity() {
            return 0;
        }

        @Override
        public List<Integer> requiredArgumentIndexes() {
            return Collections.emptyList();
        }
    }
}
