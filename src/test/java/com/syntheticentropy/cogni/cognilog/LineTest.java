package com.syntheticentropy.cogni.cognilog;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LineTest {

    @Test
    void canMakeActionLine() {
        ActionLine actionLine = new ActionLine(Arrays.asList(), Arrays.asList());
        assertTrue(actionLine.isAction());
        assertFalse(actionLine.isRule());
    }

    @Test
    void canMakeRuleLine() {
        RuleLine ruleLine = new RuleLine(Arrays.asList(), Arrays.asList(), Arrays.asList());
        assertFalse(ruleLine.isAction());
        assertTrue(ruleLine.isRule());
    }

    @Test
    void canNotMakeMismatchedLengthRule() {
        assertThrows(AssertionError.class, () -> new ActionLine(Arrays.asList(Optional.of(1)), Arrays.asList(1,2)));
    }
}
