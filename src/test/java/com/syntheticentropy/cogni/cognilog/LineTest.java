package com.syntheticentropy.cogni.cognilog;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LineTest {

    @Test
    void canMakeActionLine() {
        ActionLine actionLine = new ActionLine(Collections.emptyList(), Collections.emptyList());
        assertTrue(actionLine.isAction());
        assertFalse(actionLine.isRule());
    }

    @Test
    void canMakeRuleLine() {
        RuleLine ruleLine = new RuleLine(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        assertFalse(ruleLine.isAction());
        assertTrue(ruleLine.isRule());
    }

    @Test
    void canNotMakeMismatchedLengthRule() {
        assertThrows(AssertionError.class, () -> new ActionLine(Arrays.asList(1), Arrays.asList(1,2)));
    }
}
