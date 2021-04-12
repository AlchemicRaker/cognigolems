package com.syntheticentropy.cogni.cognilog;
import com.mojang.datafixers.util.Either;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoreTest {
    /*
    A Core:
        contains a program (not necessarily valid)
        may be runnable (if the program is valid)
        has state that is OKAY to lose (iterator states do not need to be preserved)
        will find and return solutions to the program, until all iterations have resolved
        can be restarted, which resets iterator states to their beginning (but may help preserve cached data)
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

        RuleImplementation rule5requireFirstTwo = new RuleImplementation() {
            @Override
            public int complexity() {
                return 100;
            }

            @Override
            public List<Integer> requiredArgumentIndexes() {
                return Arrays.asList(0, 1);
            }

            @Override
            public RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols) {
                List<Symbol<?>> args = requiredArgumentIndexes().stream()
                        .map(symbols::get)
                        .collect(Collectors.toList());

                return new RuleIterator(args) {
                    boolean firstRun = true;
                    @Override
                    public RuleIteratorResult next(int limit) {
                        if (firstRun) {
                            firstRun = false;
                            return new RuleIteratorResult(true, false, 1);
                        }
                        return new RuleIteratorResult(false, true, 0);
                    }
                };
            }
        };

        RuleImplementation rule5requireLast = new RuleImplementation() {
            @Override
            public int complexity() {
                return 100;
            }

            @Override
            public List<Integer> requiredArgumentIndexes() {
                return Arrays.asList(2);
            }

            @Override
            public RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols) {
                List<Symbol<?>> args = requiredArgumentIndexes().stream()
                        .map(symbols::get)
                        .collect(Collectors.toList());

                return new RuleIterator(args) {
                    boolean firstRun = true;
                    @Override
                    public RuleIteratorResult next(int limit) {
                        if (firstRun) {
                            firstRun = false;
                            return new RuleIteratorResult(true, false, 1);
                        }
                        return new RuleIteratorResult(false, true, 0);
                    }
                };
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
    void canCreateCore() {
        Core core = new Core(getValidDummyProgram());
    }

    @Test
    void canBeRunnable() {
        Core core = new Core(getValidDummyProgram());
        assertTrue(core.isRunnable());
    }

    @Test
    void canHaveAProgram() {
        Core core = new Core(getValidDummyProgram());
        core.getProgram();
    }

    @Test
    void canFindEndOfSolutions() {
        Core<?> core = new Core<Object>(getComplexProgram());
        Core.SolutionResult<?> solutionResult;
        do {
            solutionResult = core.findNextSolution();
        } while(!solutionResult.isEndOfSearch());
        // An empty optional means all solutions have been found.
        // A list, empty or not, means solutions have been found
    }
}
