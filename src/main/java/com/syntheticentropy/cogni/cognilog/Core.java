package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.Optional;

public class Core {

    private final Program program;

    public Core(Program program) {
        this.program = program;
    }

    public boolean isRunnable() {
        return program.isRunnable();
    }

    public Object getProgram() {
        return program;
    }

    public SolutionResult findNextSolution() {
        return new SolutionResult(Optional.empty(), 1, true);
    }

    public class SolutionResult {
        private final boolean isEndOfSearch;
        private final int iterationsUsed;
        private final Optional<Object> solution;
        public SolutionResult(Optional<Object> solution, int iterationsUsed, boolean isEndOfSearch) {
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

        public Optional<Object> getSolution() {
            return solution;
        }
    }
}
