package com.syntheticentropy.cogni.cognilog;

import java.util.List;
import java.util.stream.Collectors;

public class Program {

    private final List<Line> lines;
    private final boolean isRunnable;

    public Program(List<Line> lines) {
        this.lines = lines;

        //immediately try to compile this, check its validity, etc
        if(lines.size() == 0) {
            this.isRunnable = false;
            return;
        }

        final List<ActionLine> actionLines = lines.stream()
                .filter(line -> line.isAction()).map(line -> (ActionLine) line)
                .collect(Collectors.toList());
        final List<RuleLine> ruleLines = lines.stream()
                .filter(line -> line.isRule()).map(line -> (RuleLine) line)
                .collect(Collectors.toList());

        // No actions means no way to get solutions
        if(actionLines.size() == 0) {
            this.isRunnable = false;
            return;
        }
        // No rules is fine. Actions don't necessarily require rules to function.



        this.isRunnable = true;
    }

    public boolean isRunnable() {
        return isRunnable;
    }

    public List<Line> getLines() {
        return lines;
    }
}
