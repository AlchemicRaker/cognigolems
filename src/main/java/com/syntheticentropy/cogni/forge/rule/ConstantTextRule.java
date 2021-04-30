package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.ConstantRuleLine;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.BlockTypeValue;
import com.syntheticentropy.cogni.forge.symbol.TextValue;

import java.util.Collections;
import java.util.Optional;

public class ConstantTextRule extends ConstantRuleLine<Solution> {
    public ConstantTextRule(Integer argumentSymbol, TextValue textValue) {
        super(Collections.singletonList(Optional.ofNullable(argumentSymbol)),
                Collections.singletonList(BaseValue.Type.Text.ordinal()),
                Collections.singletonList(textValue));
    }
}
