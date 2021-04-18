package com.syntheticentropy.cogni.cognilog;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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


    public static <T> Program<T> getValidDummyProgram() {
        return Program.compileProgram(Arrays.asList(new ActionLine<T>(Arrays.asList(),Arrays.asList())));
    }

    public static <T> Program<T> getComplexProgram(ActionLine<T> action) {
//        ActionLine<T> action = new ActionLine<T>(
//                Arrays.asList(6),
//                Arrays.asList(1));
//        action.createResult(Arrays.asList());


        RuleLine<T> constRule1 = new ConstantRuleLine<T>(
                Arrays.asList(Optional.of(1), Optional.of(2), Optional.of(4)),
                Arrays.asList(1, 1, 1),
                Arrays.asList(10, 20, 40),
                200);

        RuleLine<T> constRule2 = new ConstantRuleLine<T>(
                Arrays.asList(Optional.of(3)),
                Arrays.asList(1),
                Arrays.asList(30),
                150);

        RuleLine<T> constRule3 = new ConstantRuleLine<T>(
                Arrays.asList(Optional.of(4)),
                Arrays.asList(1),
                Arrays.asList(40));

        RuleLine<T> constRule4 = new ConstantRuleLine<T>(
                Arrays.asList(Optional.of(5)),
                Arrays.asList(1),
                Arrays.asList(50));

        RuleImplementation<T> rule5requireFirstTwo = new RuleImplementation<T>() {
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
                List<Optional<Symbol<Object>>> args = Stream.of(Optional.of(3),Optional.of(4),Optional.of(6))
                        .map(maybeArgIndex -> maybeArgIndex.map(index -> {
                    if(!symbols.containsKey(index)) symbols.put(index, new Symbol<Object>(index));
                    return (Symbol<Object>) symbols.get(index);
                })).collect(Collectors.toList());

                return new RuleIterator(args) {
                    boolean firstRun = true;
                    @Override
                    public RuleIteratorResult next(int limit) {
                        if (firstRun) {
                            firstRun = false;
                            List<Integer> values = Arrays.asList(30, 40, 60);
                            assert values.size() == args.size();
                            for (int i = 0; i < values.size(); i++){
                                Optional<Symbol<Object>> arg = args.get(i);
                                if(!arg.isPresent()) continue;
                                Symbol<Object> s = arg.get();
                                if(s.getValue().isPresent()) {
                                    continue;
                                }
                                s.setValue(values.get(i));
                            }
                            return new RuleIteratorResult(true, false, 1);
                        }
                        return new RuleIteratorResult(false, true, 0);
                    }
                };
            }
        };

        RuleImplementation<T> rule5requireLast = new RuleImplementation<T>() {
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
                List<Optional<Symbol<Object>>> args = Stream.of(Optional.of(3),Optional.of(4),Optional.of(6))
                        .map(maybeArgIndex -> maybeArgIndex.map(index -> {
                            if(!symbols.containsKey(index)) symbols.put(index, new Symbol<Object>(index));
                            return (Symbol<Object>) symbols.get(index);
                        })).collect(Collectors.toList());

                return new RuleIterator(args) {
                    boolean firstRun = true;
                    @Override
                    public RuleIteratorResult next(int limit) {
                        if (firstRun) {
                            firstRun = false;
                            List<Integer> values = Arrays.asList(30, 40, 60);
                            assert values.size() == args.size();
                            for (int i = 0; i < values.size(); i++){
                                Optional<Symbol<Object>> arg = args.get(i);
                                if(!arg.isPresent()) continue;
                                Symbol<Object> s = arg.get();
                                if(s.getValue().isPresent()) {
                                    continue;
                                }
                                s.setValue(values.get(i));
                            }
                            return new RuleIteratorResult(true, false, 1);
                        }
                        return new RuleIteratorResult(false, true, 0);
                    }
                };
            }
        };

        RuleLine<T> rule5 = new RuleLine<T>(
                Arrays.asList(Optional.of(3), Optional.of(4), Optional.of(6)),
                Arrays.asList(5, 5, 5),
                Arrays.asList(rule5requireLast, rule5requireFirstTwo)
        );

        return Program.compileProgram(Arrays.asList(action, constRule1, constRule2, constRule3, constRule4, rule5));
    }

    static class ActionLineString extends ActionLine<String> {

        public ActionLineString() {
            super(Arrays.asList(4, 6), Arrays.asList(1, 1));
        }

        public String createResult(List<Symbol<?>> symbols) {
            return symbols.stream().map(symbol -> {
                return "[" + symbol.getIndex() + "]=" + symbol.getValue().get();
            }).collect(Collectors.joining(","));
        }

    }

    @Test
    void canMakeProgramFromLines() {
        Program<String> program = getComplexProgram(new ActionLineString());
        assertTrue(program.isRunnable());
        program.getQueries().forEach(query -> System.out.println(query.toDependencyString()));
    }

    @Test
    void programWithoutLinesIsInvalid() {
        Program<Object> program = Program.compileProgram(Arrays.asList());
        assertFalse(program.isRunnable());
    }

    @Test
    void programWithoutActionsIsInvalid() {
        Program<Object> program = Program.compileProgram(Arrays.asList(new RuleLine<Object>(Arrays.asList(),Arrays.asList(),Arrays.asList())));
        assertFalse(program.isRunnable());
    }

}
