package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionLine<T> extends Line<T> {

    final private List<Integer> requiredArgumentSymbols;

    public ActionLine(List<Integer> argumentSymbols, List<Integer> argumentTypes) {
        super(argumentSymbols.stream().map(Optional::of).collect(Collectors.toList()), argumentTypes);
        this.requiredArgumentSymbols = argumentSymbols;
    }

    public List<Integer> getRequiredSymbols() {
        return this.requiredArgumentSymbols;
    }

    public boolean isAction() {
        return true;
    }

    public T createResult(List<Symbol<?>> symbols) {
        return null;
    }

    // Dereference using requiredArgumentSymbols and call the list variant
    public T createResult(Map<Integer, Symbol<?>> symbols) {
        return createResult(requiredArgumentSymbols.stream()
                .map(symbols::get)
                .collect(Collectors.toList()));
    }
}
