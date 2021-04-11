package com.syntheticentropy.cogni.cognilog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Program {

    public static Program compileProgram(List<Line> lines) {
        //immediately try to compile this, check its validity, etc
        if(lines.size() == 0) {
            return new Program(Collections.emptyList(), false);
        }

        final List<ActionLine> actionLines = lines.stream()
                .filter(Line::isAction).map(line -> (ActionLine) line)
                .collect(Collectors.toList());
        final List<RuleLine> ruleLines = lines.stream()
                .filter(Line::isRule).map(line -> (RuleLine) line)
                .collect(Collectors.toList());

        // No actions means no way to get solutions
        if(actionLines.size() == 0) {
            return new Program(Collections.emptyList(), false);
        }
        // No rules is fine. Actions don't necessarily require rules to function.

        // TODO: add action grouping, so multiple actions must be matched and execute in sequence.
        final List<CompiledQuery> queries = actionLines.stream()
                .map(actionLine -> CompiledQuery.compileQuery(Arrays.asList(actionLine), ruleLines))
                .filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toList());

        return new Program(queries, queries.size() > 0);
    }

    private final List<CompiledQuery> queries;
    private final boolean isRunnable;

    private Program(List<CompiledQuery> queries, boolean isRunnable) {
        this.queries = queries;
        this.isRunnable = isRunnable;
        assert (queries.size() > 0) == isRunnable;
    }

    public boolean isRunnable() {
        return isRunnable;
    }

    public List<CompiledQuery> getQueries() {
        return queries;
    }
}
