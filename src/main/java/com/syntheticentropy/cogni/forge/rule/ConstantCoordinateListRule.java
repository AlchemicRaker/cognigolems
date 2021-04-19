package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.*;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConstantCoordinateListRule extends RuleLine<Solution> {

    public ConstantCoordinateListRule(Integer argumentSymbol, List<CoordinateValue> coordinateValues) {
        super(Collections.singletonList(Optional.ofNullable(argumentSymbol)),
                Collections.singletonList(BaseValue.Type.Coordinate.ordinal()),
                Collections.singletonList(new ConstantCoordinateListRuleImplementation(
                        argumentSymbol, coordinateValues)));
    }

    public static class ConstantCoordinateListRuleImplementation extends RuleImplementation<Solution> {

        // used later in the Iterable implementation, as the only values for each argument
        private final List<CoordinateValue> values;
        private final int complexity;
        private final Integer argumentSymbol;

        public ConstantCoordinateListRuleImplementation(Integer argumentSymbol, List<CoordinateValue> values) {
            this.argumentSymbol = argumentSymbol;
            this.values = values;
            this.complexity = 0;
        }

        public ConstantCoordinateListRuleImplementation(Integer argumentSymbol, List<CoordinateValue> values, int complexity) {
            this.argumentSymbol = argumentSymbol;
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
//            List<Optional<Symbol<Object>>> args = this.argumentSymbols.stream().map(maybeArgIndex -> maybeArgIndex.map(index -> {
//                if(!symbols.containsKey(index)) symbols.put(index, new Symbol<Object>(index));
//                return (Symbol<Object>) symbols.get(index);
//            })).collect(Collectors.toList());
            if(!symbols.containsKey(argumentSymbol)) symbols.put(argumentSymbol, new Symbol<CoordinateValue>(argumentSymbol));

            List<Optional<Symbol<Object>>> args = Collections.singletonList(Optional.of((Symbol<Object>) symbols.get(argumentSymbol)));

            return new RuleIterator(args) {
                int counter = 0;
                @Override
                public RuleIteratorResult next(int limit) {
                    if (counter >= values.size()) {
                        return new RuleIteratorResult(false, true, 1);
//                        firstRun = false;
                        // assign the values to all the symbols
//                        assert values.size() == args.size();
//                        for (int i = 0; i < values.size(); i++){
//                            Optional<Symbol<Object>> arg = args.get(i);
//                            if(!arg.isPresent()) continue;
//                            Symbol<Object> s = arg.get();
//                            // if s already has a value, we must assert a match
//                            // if s has no value, we c
//                            s.setValue(values.get(i));
//                        }
//                        return new RuleIteratorResult(true, false, 1);
                    }
                    Optional<Symbol<Object>> arg = args.get(0);
                    if(!arg.isPresent()) {
                        return new RuleIteratorResult(false, true, 1);
                    }
                    Symbol<Object> s = arg.get();
                    s.setValue(values.get(counter));
//                    Optional<CoordinateValue> coordinateValue = .map(Symbol::getValue).map(Optional::get).map(CoordinateValue::getCoordinateValue);

                    counter++;
                    return new RuleIteratorResult(true, false, 1);
                }
            };
        }
    }
}
