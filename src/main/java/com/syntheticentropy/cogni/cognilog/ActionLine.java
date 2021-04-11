package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Optional;

public class ActionLine extends Line {

    public ActionLine(List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes) {
        super(argumentSymbols, argumentTypes);
    }

    public boolean isAction() {
        return true;
    }
}
