package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionLine extends Line {

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
}
