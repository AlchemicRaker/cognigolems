package com.syntheticentropy.cogni.cognilog;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class RuleImplementation<T> {
    public abstract int complexity();

    public abstract List<Integer> requiredArgumentIndexes();

    public abstract RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols);

    public Optional<List<Integer>> requiredArgumentSymbols(RuleLine<T> ruleLine) {
        List<Optional<Integer>> argumentSymbols = ruleLine.getArgumentSymbols();
        List<Optional<Integer>> dereferencedArgumentSymbols = requiredArgumentIndexes().stream()
                .map(argumentSymbols::get)
                .collect(Collectors.toList());

        if (!dereferencedArgumentSymbols.stream().allMatch(Optional::isPresent)) {
            return Optional.empty();
        }

        return Optional.of(dereferencedArgumentSymbols.stream().map(Optional::get).collect(Collectors.toList()));
    }

    public String toDependencyString(RuleLine<T> ruleLine) {
        return "RI[$" + complexity() + "] " +
                requiredArgumentSymbols(ruleLine).orElse(Collections.emptyList()) +
                " -> " +
                ruleLine.getArgumentSymbols().stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }
}
