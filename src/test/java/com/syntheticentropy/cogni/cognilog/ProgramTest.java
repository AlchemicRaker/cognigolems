package com.syntheticentropy.cogni.cognilog;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProgramTest {
    /* A program:
            has lines, at least one:
                has actions, at least one
                may have rules, zero or more

            tries to compile the lines:
                symbols must be of a consistent type across all arguments it is used in
                at least one action must successfully be defined
                determine which implementation of each line to use
                determine order to run lines to solve for an action

       A line:
            has symbols optionally set for each argument
            has type expectations set for each argument

       A rule implementation:
            has specific inputs and outputs
            provides an iterator that defines ALL of its outputs

       An action is a line that:
            contains only inputs
            can create a result from all the inputs
     */

    @Test
    void canMakeProgramFromLines() {
        Program program = new Program(Arrays.asList());
    }

    @Test
    void programWithoutLinesIsInvalid() {
        Program program = new Program(Arrays.asList());
        assertFalse(program.isRunnable());
    }
}
