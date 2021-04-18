package com.syntheticentropy.cogni.cognilog;

import java.util.*;
import java.util.stream.Collectors;

public class Core<T> {

    /* Core contains a program and running state

     */

    private final Program<T> program;

    public Core(Program<T> program) {
        this.program = program;
    }

    public boolean isRunnable() {
        return program.isRunnable();
    }

    public Program<T> getProgram() {
        return program;
    }

    int currentQueryIndex = 0;
    CompiledQuery<T> currentQuery = null;
    boolean foundQuerySolution = false;
    List<RuleIterator> stack = new ArrayList<>();
    Map<Integer, Symbol<?>> symbols = new HashMap<>();

    public SolutionResult<T> findNextSolution() {
        if (!getProgram().isRunnable()) {
            return new SolutionResult<>(null, 0, true);
        }
        // the stack is always valid, even if it is empty.
        // algorithm:
        //   if the stack is empty, push the first iterator onto the stack
        //   run the topmost iterator on the stack, get a result
        //   track the cost from the result
        //   if the result bound a value, push the next iterator onto the stack
        //   if the result is the last iteration, pop this iterator from the stack

        if (stack.isEmpty()) {
            // Figure out which query to run next
            if(currentQuery == null) {
                // load the first query
                currentQueryIndex = 0;
                currentQuery = getProgram().getQueries().get(currentQueryIndex);
                foundQuerySolution = false;
            } else if(foundQuerySolution) {
                // found at least one solution last time, so try this query again until no solution is found
                foundQuerySolution = false;
            } else {
                // load the next query, wrapping around if necessary
                currentQueryIndex = (currentQueryIndex + 1) % getProgram().getQueries().size();
                currentQuery = getProgram().getQueries().get(currentQueryIndex);
                foundQuerySolution = false;
            }

            // Put the first iterator on the stack
            // When it is removed, the query is complete
            stack.add(currentQuery.getRules().get(stack.size()).getImplementation().createRuleIterator(symbols));
        }

        int totalLimit = 100;
        int remainingLimit = totalLimit;
        boolean foundSolution = false;
        while (remainingLimit > 0) {
            RuleIterator ruleIterator = stack.get(stack.size()-1);
            RuleIterator.RuleIteratorResult result = ruleIterator.next(remainingLimit);

            remainingLimit -= result.cost;
            if (result.boundSymbols) {
                if (stack.size() < currentQuery.getRules().size()) {
                    // put the next iterator on the stack!
                    stack.add(currentQuery.getRules().get(stack.size()).getImplementation().createRuleIterator(symbols));
                } else {
                    // or if none left, we've found a solution, so stop processing
                    foundSolution = true;
                    break;
                }
            } else if (result.lastIteration) {
                // remove this iterator from the stack
                stack.remove(stack.size()-1);

                // Empty stack? ignore the remainingLimit and stop processing
                if (stack.isEmpty()) {
                    break;
                }
            } else {
                assert remainingLimit <= 0;
            }
        }

        if (stack.isEmpty()) {
            // no solution, query has completed
            return new SolutionResult<>(totalLimit - remainingLimit, true);
        }

        if (!foundSolution) {
            // no solution, query is ongoing
            return new SolutionResult<>(totalLimit - remainingLimit, false);
        }

        // found a solution, query is ongoing
        List<T> results = currentQuery.getActions().stream()
                .map(action -> action.createResult(symbols))
                .collect(Collectors.toList());
//        currentQuery.getActions().stream()
//                .map(action -> action.)

        return new SolutionResult<>(results, 1, true);
    }

    public static class SolutionResult<T> {
        private final boolean isEndOfSearch;
        private final int iterationsUsed;
        private final List<T> solution;

        public SolutionResult(int iterationsUsed, boolean isEndOfSearch) {
            this(null, iterationsUsed, isEndOfSearch);
        }

        public SolutionResult(List<T> solution, int iterationsUsed, boolean isEndOfSearch) {
            this.isEndOfSearch = isEndOfSearch;
            this.iterationsUsed = iterationsUsed;
            this.solution = solution;
        }

        public boolean isEndOfSearch() {
            return isEndOfSearch;
        }

        public int getIterationsUsed() {
            return iterationsUsed;
        }

        public Optional<List<T>> getSolution() {
            return Optional.of(solution);
        }
    }
}
