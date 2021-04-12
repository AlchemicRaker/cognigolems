package com.syntheticentropy.cogni.cognilog;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CompiledQuery<T> {

    public static <T> Optional<CompiledQuery<T>> compileQuery(List<ActionLine<T>> actions, List<RuleLine<T>> rules) {
        // Find an order and combination of rules that provides all of the required symbols

        final List<Integer> requiredSymbols = actions.stream()
                .flatMap(actionLine -> actionLine.getRequiredSymbols().stream())
                .distinct()
                .collect(Collectors.toList());

        // First find all rules that are connected to the action's symbols
        List<Integer> connectedSymbols = new ArrayList<>(requiredSymbols);
        List<RuleLine<T>> unconnectedRules = new ArrayList<>(rules);
        List<RuleLine<T>> connectedRules = new ArrayList<>();
        List<RuleLine<T>> nextConnectedRules;
        do {
            // Do this thing so we can reference it in the lambda
            List<Integer> finalConnectedSymbols = connectedSymbols;
            nextConnectedRules = unconnectedRules.stream()
                    .filter(ruleLine -> ruleLine.getDefinedSymbols().stream().anyMatch(finalConnectedSymbols::contains))
                    .collect(Collectors.toList());

            if (nextConnectedRules.size() > 0) {
                connectedRules.addAll(nextConnectedRules);
                unconnectedRules.removeAll(nextConnectedRules);
                connectedSymbols = Stream.concat(connectedSymbols.stream(),
                        connectedRules.stream().flatMap(rule -> rule.getDefinedSymbols().stream()))
                        .distinct()
                        .collect(Collectors.toList());
            }
        } while(nextConnectedRules.size() > 0);

        // We've now identified all rules that are connected to what we're doing!
        // We must track them and make sure they're all used

        List<RuleLine<T>> unusedRules = new ArrayList<>(connectedRules);
        List<Integer> knownSymbols = Collections.emptyList();
        List<IndexedRuleLine<T>> compiledRules = new ArrayList<>();

        Optional<IndexedRuleLine<T>> nextRuleLine;

        Comparator<IndexedRuleLine<T>> indexedRuleLineComparator = Comparator.comparingInt(e -> e.getImplementation().complexity());

        // Well this is working, but it's wrong
        // It should be attempting to use all rules
        // But if all rules can't be used, it's even more invalid invalid
        while (unusedRules.size() > 0) {
            Stream<IndexedRuleLine<T>> implementationsStream = unusedRules.stream()
                    .flatMap(CompiledQuery::indexedRuleLinesFrom)
                    .sorted(indexedRuleLineComparator);

            // Do this thing so we can reference it in the lambda
            List<Integer> finalKnownSymbols = knownSymbols;
            nextRuleLine = implementationsStream.filter(implementation -> {
                // if not all inputs of this implementation have symbols defined, optional will be empty
                final Optional<List<Integer>> maybeRequiredSymbols = implementation.getImplementation().requiredArgumentSymbols(implementation.getRuleLine());

                // match if all required symbols are known
                return maybeRequiredSymbols.filter(finalKnownSymbols::containsAll).isPresent();
            }).findFirst();


            // If we didn't find a way to resolve another rule, the query can't possibly be completed
            if(!nextRuleLine.isPresent()) {
                return Optional.empty();
            }


            // What are the newly discovered symbols?
            List<Integer> discoveredSymbolStream = nextRuleLine.get().getRuleLine().getArgumentSymbols().stream()
                    .filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toList());

            // Add to the known symbols
            knownSymbols = Stream.concat(knownSymbols.stream(), discoveredSymbolStream.stream())
                    .distinct()
                    .collect(Collectors.toList());

            // include use rules only once
            unusedRules.remove(nextRuleLine.get().getRuleLine());

            compiledRules.add(nextRuleLine.get());
        }


        // All symbols found
        // All connected rules are compiled and ordered

        return Optional.of(new CompiledQuery<T>(actions, compiledRules));
    }

    private final List<ActionLine<T>> actions;
    private final List<IndexedRuleLine<T>> rules;

    public List<ActionLine<T>> getActions() {
        return actions;
    }

    public List<IndexedRuleLine<T>> getRules() {
        return rules;
    }

    private CompiledQuery(List<ActionLine<T>> actions, List<IndexedRuleLine<T>> rules) {
        this.actions = actions;
        this.rules = rules;
    }

    private static <T> Stream<IndexedRuleLine<T>> indexedRuleLinesFrom(RuleLine<T> ruleLine) {
        return IntStream.range(0, ruleLine.getRuleImplementations().size()).boxed()
                .map(index -> new IndexedRuleLine<T>(ruleLine, index));
    }

    public String toDependencyString() {
        return "CompiledQuery{\n" +
                " actionSymbols=" + actions.stream().flatMap(action -> action.getRequiredSymbols().stream()).distinct().map(Object::toString).collect(Collectors.joining(",")) +
                ",\n rules=\n  " + rules.stream().map(IndexedRuleLine::toDependencyString).collect(Collectors.joining("\n  ")) +
                "\n}";
    }

    public static class IndexedRuleLine<T> {
        private final RuleLine<T> ruleLine;
        private final int implementationIndex;
        private IndexedRuleLine(RuleLine<T> ruleLine, int implementationIndex) {
            this.ruleLine = ruleLine;
            this.implementationIndex = implementationIndex;
        }

        public RuleLine<T> getRuleLine() {
            return ruleLine;
        }

        public RuleImplementation getImplementation() {
            return ruleLine.getRuleImplementations().get(implementationIndex);
        }

        public int getImplementationIndex() {
            return implementationIndex;
        }

        public String toDependencyString() {
            return getImplementation().toDependencyString(getRuleLine());
        }
    }
}
