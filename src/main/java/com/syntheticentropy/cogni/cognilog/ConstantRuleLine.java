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
        super(argumentSymbols, argumentTypes, Collections.singletonList(
                new ConstantRuleImplementation<T>(argumentSymbols, values)));
    }

    public ConstantRuleLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes, List<Object> values, int complexity) {
        super(argumentSymbols, argumentTypes, Collections.singletonList(
                new ConstantRuleImplementation<T>(argumentSymbols, values, complexity)));
    }

    public static class ConstantRuleImplementation<T> extends RuleImplementation<T> {

        // used later in the Iterable implementation, as the only values for each argument
        private final List<Object> values;
        private final int complexity;
        private final List<Optional<Integer>> argumentSymbols;

        public ConstantRuleImplementation(List<Optional<Integer>> argumentSymbols, List<Object> values) {
            this.argumentSymbols = argumentSymbols;
            this.values = values;
            this.complexity = 0;
        }

        public ConstantRuleImplementation(List<Optional<Integer>> argumentSymbols, List<Object> values, int complexity) {
            this.argumentSymbols = argumentSymbols;
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
            List<Optional<Symbol<Object>>> args = this.argumentSymbols.stream().map(maybeArgIndex -> maybeArgIndex.map(index -> {
                if(!symbols.containsKey(index)) symbols.put(index, new Symbol<Object>(index));
                return (Symbol<Object>) symbols.get(index);
            })).collect(Collectors.toList());

            return new RuleIterator(args) {
                boolean firstRun = true;
                @Override
                public RuleIteratorResult next(int limit) {
                    if (firstRun) {
                        firstRun = false;
                        // assign the values to all the symbols
                        assert values.size() == args.size();
                        for (int i = 0; i < values.size(); i++){
                            Optional<Symbol<Object>> arg = args.get(i);
                            if(!arg.isPresent()) continue;
                            Symbol<Object> s = arg.get();
                            // if s already has a value, we must assert a match
                            // if s has no value, we c
                            s.setValue(values.get(i));
                        }
                        return new RuleIteratorResult(true, false, 1);
                    }
                    return new RuleIteratorResult(false, true, 0);
                }
            };
        }
    }
}
