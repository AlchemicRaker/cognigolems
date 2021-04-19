package com.syntheticentropy.cogni.forge.action;

import com.syntheticentropy.cogni.cognilog.ActionLine;
import com.syntheticentropy.cogni.cognilog.Symbol;
import com.syntheticentropy.cogni.forge.solution.SampleSolution;
import com.syntheticentropy.cogni.forge.solution.Solution;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SampleAction extends ActionLine<Solution> {

    // a basic action that always succeeds and has no connections
    public SampleAction(List<Integer> argumentSymbols, List<Integer> argumentTypes) {
        super(argumentSymbols, argumentTypes);
    }

    @Override
    public Solution createResult(List<Symbol<?>> symbols) {
        assert symbols.size() == 0;
        return new SampleSolution("Hello sample action result!");
    }
}
