package com.syntheticentropy.cogni.log;

import java.util.List;

public interface IActionGoal {
    IAction getAction();

    // an ordered chain of iterables, optimized and ready to run
    List<IFact> getFacts();
}
