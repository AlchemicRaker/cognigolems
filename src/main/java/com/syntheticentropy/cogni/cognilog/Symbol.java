package com.syntheticentropy.cogni.cognilog;

import java.util.Optional;

public class Symbol<T> {
    // an instance of a symbol
    // it has an index
    // it can have a bound value

    private final int index;
    private T value = null;

    public Symbol(int index) {
        this.index = index;
        // start with no assigned value
    }

    public int getIndex() {
        return index;
    }

    public Optional<T> getValue() {
        return Optional.ofNullable(value);
    }

    public void setValue(T value) {
        this.value = value;
    }
}
