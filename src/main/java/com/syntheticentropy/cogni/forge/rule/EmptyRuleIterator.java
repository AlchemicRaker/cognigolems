package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.RuleIterator;
import com.syntheticentropy.cogni.cognilog.Symbol;

import java.util.List;
import java.util.Optional;

public class EmptyRuleIterator extends RuleIterator {
    public EmptyRuleIterator() {
        super(null);
    }

    @Override
    public RuleIteratorResult next(int limit) {
        return new RuleIteratorResult(false, true, 1);
    }
}
