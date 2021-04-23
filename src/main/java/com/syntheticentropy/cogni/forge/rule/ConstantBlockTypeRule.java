package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.ConstantRuleLine;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.BlockTypeValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;

import java.util.Collections;
import java.util.Optional;

public class ConstantBlockTypeRule extends ConstantRuleLine<Solution> {
    public ConstantBlockTypeRule(Integer argumentSymbol, BlockTypeValue blockTypeValue) {
        super(Collections.singletonList(Optional.ofNullable(argumentSymbol)),
                Collections.singletonList(BaseValue.Type.BlockType.ordinal()),
                Collections.singletonList(blockTypeValue));
    }
}
