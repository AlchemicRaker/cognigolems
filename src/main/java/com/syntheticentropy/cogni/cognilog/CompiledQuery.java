package com.syntheticentropy.cogni.cognilog;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CompiledQuery {

    public static Optional<CompiledQuery> compileQuery(List<ActionLine> actions, List<RuleLine> rules) {
        // Find an order and combination of rules that provides all of the required symbols

        final List<Integer> requiredSymbols = actions.stream()
                .flatMap(actionLine -> actionLine.getRequiredSymbols().stream())
                .distinct()
                .collect(Collectors.toList());

        // First find all rules that are connected to the action's symbols
        List<Integer> connectedSymbols = new ArrayList<>(requiredSymbols);
        List<RuleLine> unconnectedRules = new ArrayList<>(rules);
        List<RuleLine> connectedRules = new ArrayList<>();
        List<RuleLine> nextConnectedRules;
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

        List<RuleLine> unusedRules = new ArrayList<>(connectedRules);
        List<Integer> knownSymbols = Collections.emptyList();
        List<IndexedRuleLine> compiledRules = new ArrayList<>();

        Optional<IndexedRuleLine> nextRuleLine;

        Comparator<IndexedRuleLine> indexedRuleLineComparator = Comparator.comparingInt(e -> e.getImplementation().complexity());

        // Well this is working, but it's wrong
        // It should be attempting to use all rules
        // But if all rules can't be used, it's even more invalid invalid
        while (unusedRules.size() > 0) {
            Stream<IndexedRuleLine> implementationsStream = unusedRules.stream()
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

        return Optional.of(new CompiledQuery(actions, compiledRules));
    }

    private final List<ActionLine> actions;
    private final List<IndexedRuleLine> rules;

    public List<ActionLine> getActions() {
        return actions;
    }

    public List<IndexedRuleLine> getRules() {
        return rules;
    }

    private CompiledQuery(List<ActionLine> actions, List<IndexedRuleLine> rules) {
        this.actions = actions;
        this.rules = rules;
    }

    private static Stream<IndexedRuleLine> indexedRuleLinesFrom(RuleLine ruleLine) {
        return IntStream.range(0, ruleLine.getRuleImplementations().size()).boxed()
                .map(index -> new IndexedRuleLine(ruleLine, index));
    }

    public String toDependencyString() {
        return "CompiledQuery{\n" +
                " actionSymbols=" + actions.stream().flatMap(action -> action.getRequiredSymbols().stream()).distinct().map(Object::toString).collect(Collectors.joining(",")) +
                ",\n rules=\n  " + rules.stream().map(IndexedRuleLine::toDependencyString).collect(Collectors.joining("\n  ")) +
                "\n}";
    }

    public static class IndexedRuleLine {
        private final RuleLine ruleLine;
        private final int implementationIndex;
        private IndexedRuleLine(RuleLine ruleLine, int implementationIndex) {
            this.ruleLine = ruleLine;
            this.implementationIndex = implementationIndex;
        }

        public RuleLine getRuleLine() {
            return ruleLine;
        }

        public RuleLine.RuleImplementation getImplementation() {
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
