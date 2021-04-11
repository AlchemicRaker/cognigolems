package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Optional;

public abstract class Line {
    /*
       A line:
            has one or more implementations with different input requirements
            has symbols optionally set for each argument

     */

    private final List<Optional<Integer>> argumentSymbols;
    private final List<Integer> argumentTypes;

    public Line(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes) {
        this.argumentSymbols = argumentSymbols;
        this.argumentTypes = argumentTypes;
        assert argumentSymbols.size() == argumentTypes.size();
    }

    public List<Optional<Integer>> getArgumentSymbols() {
        return argumentSymbols;
    }

    public boolean isAction() {
        return false;
    }

    public boolean isRule() {
        return false;
    }
}
