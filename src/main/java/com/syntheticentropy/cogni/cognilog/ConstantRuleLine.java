package com.syntheticentropy.cogni.cognilog;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConstantRuleLine<T> extends RuleLine<T> {

    /*
        Every argument has a single, known value
        All arguments are outputs
        No arguments are inputs
     */

    public ConstantRuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<Object> values) {
        super(argumentSymbols, argumentTypes, Collections.singletonList(new ConstantRuleImplementation<T>(values)));
    }

    public ConstantRuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<Object> values, int complexity) {
        super(argumentSymbols, argumentTypes, Collections.singletonList(new ConstantRuleImplementation<T>(values, complexity)));
    }

    public static class ConstantRuleImplementation<T> extends RuleImplementation<T> {

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

        @Override
        public RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols) {
            List<Symbol<?>> args = requiredArgumentIndexes().stream()
                    .map(symbols::get)
                    .collect(Collectors.toList());

            return new RuleIterator(args) {
                boolean firstRun = true;
                @Override
                public RuleIteratorResult next(int limit) {
                    if (firstRun) {
                        firstRun = false;
                        return new RuleIteratorResult(true, false, 1);
                    }
                    return new RuleIteratorResult(false, true, 0);
                }
            };
        }
    }
}
