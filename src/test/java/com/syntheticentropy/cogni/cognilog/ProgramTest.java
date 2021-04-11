package com.syntheticentropy.cogni.cognilog;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

    private static Program getValidDummyProgram() {
        return Program.compileProgram(Arrays.asList(new ActionLine(Arrays.asList(),Arrays.asList())));
    }

    private static Program getComplexProgram() {
        ActionLine action = new ActionLine(
                Arrays.asList(6),
                Arrays.asList(1));

        RuleLine constRule1 = new ConstantRuleLine(
                Arrays.asList(Optional.of(1), Optional.of(2), Optional.of(4)),
                Arrays.asList(1, 1, 1),
                Arrays.asList(5, 5, 5),
                200);

        RuleLine constRule2 = new ConstantRuleLine(
                Arrays.asList(Optional.of(3)),
                Arrays.asList(1),
                Arrays.asList(5),
                150);

        RuleLine constRule3 = new ConstantRuleLine(
                Arrays.asList(Optional.of(4)),
                Arrays.asList(1),
                Arrays.asList(5));

        RuleLine constRule4 = new ConstantRuleLine(
                Arrays.asList(Optional.of(5)),
                Arrays.asList(1),
                Arrays.asList(5));

        RuleLine.RuleImplementation rule5requireFirstTwo = new RuleLine.RuleImplementation() {
            @Override
            public int complexity() {
                return 100;
            }

            @Override
            public List<Integer> requiredArgumentIndexes() {
                return Arrays.asList(0, 1);
            }
        };

        RuleLine.RuleImplementation rule5requireLast = new RuleLine.RuleImplementation() {
            @Override
            public int complexity() {
                return 100;
            }

            @Override
            public List<Integer> requiredArgumentIndexes() {
                return Arrays.asList(2);
            }
        };

        RuleLine rule5 = new RuleLine(
                Arrays.asList(Optional.of(3), Optional.of(4), Optional.of(6)),
                Arrays.asList(5, 5, 5),
                Arrays.asList(rule5requireLast, rule5requireFirstTwo)
        );

        return Program.compileProgram(Arrays.asList(action, constRule1, constRule2, constRule3, constRule4, rule5));
    }

    @Test
    void canMakeProgramFromLines() {
        Program program = getComplexProgram();
        assertTrue(program.isRunnable());
        program.getQueries().forEach(query -> System.out.println(query.toDependencyString()));
    }

    @Test
    void programWithoutLinesIsInvalid() {
        Program program = Program.compileProgram(Arrays.asList());
        assertFalse(program.isRunnable());
    }

    @Test
    void programWithoutActionsIsInvalid() {
        Program program = Program.compileProgram(Arrays.asList(new RuleLine(Arrays.asList(),Arrays.asList(),Arrays.asList())));
        assertFalse(program.isRunnable());
    }

}
