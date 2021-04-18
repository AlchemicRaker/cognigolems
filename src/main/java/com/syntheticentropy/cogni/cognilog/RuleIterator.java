package com.syntheticentropy.cogni.cognilog;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class RuleIterator {
    // created by a RuleImplementation at the time it is put onto the stack
    // inputs: array of symbols, possibly with assigned values
    // outputs: all symbols must have assigned values
    // return true if there is more results, false if no more results

    public final List<Optional<Symbol<Object>>> symbols;

    public RuleIterator(List<Optional<Symbol<Object>>> symbols) {
        this.symbols = symbols;
    }

    // if there is any more iterations
    //   bind values to symbols and return true
    // if there are no more iterations
    //   return false
    public abstract RuleIteratorResult next(int limit);

    public static class RuleIteratorResult {
        public final boolean boundSymbols;
        public final boolean lastIteration;
        public final int cost;

        public RuleIteratorResult(boolean boundSymbols, boolean lastIteration, int cost) {
            this.boundSymbols = boundSymbols;
            this.lastIteration = lastIteration;
            this.cost = cost;
        }
    }
}
