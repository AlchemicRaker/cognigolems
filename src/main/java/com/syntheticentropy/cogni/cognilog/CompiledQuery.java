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

        List<RuleLine> unusedRules = new ArrayList<>(rules);
        List<Integer> remainingSymbols = new ArrayList<>(requiredSymbols);
        List<Integer> knownSymbols = Collections.emptyList();
        List<IndexedRuleLine> compiledRules = new ArrayList<>();

        Optional<IndexedRuleLine> nextRuleLine;

        Comparator<IndexedRuleLine> indexedRuleLineComparator = Comparator.comparingInt(e -> e.getImplementation().complexity());

        // Well this is working, but it's wrong
        // It should be attempting to use all rules
        // If remainingsymbols is > 0 at the end, then it's simply an invalid program
        // But if all rules can't be used, it's even more invalid invalid
        while (remainingSymbols.size() > 0) {
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

            // If we didn't find any more sources of symbols, the query can't possibly be completed
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

            // Remove from list of symbols we still need to find
            remainingSymbols = remainingSymbols.stream()
                    .filter(symbol -> !discoveredSymbolStream.contains(symbol))
                    .collect(Collectors.toList());

            // No way this ruleLine can help us any more in future iterations
            unusedRules.remove(nextRuleLine.get().getRuleLine());

            compiledRules.add(nextRuleLine.get());
        }

        // All symbols found
        // All necessary rules are compiled and ordered

        // Now tack on ANY remaining rules using their least complex

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
