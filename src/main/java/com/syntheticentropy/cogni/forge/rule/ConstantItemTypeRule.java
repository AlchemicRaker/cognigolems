package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.ConstantRuleLine;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.BlockTypeValue;
import com.syntheticentropy.cogni.forge.symbol.ItemTypeValue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConstantItemTypeRule extends ConstantRuleLine<Solution> {
    public ConstantItemTypeRule(Integer argumentSymbol, ItemTypeValue itemTypeValue) {
        super(Collections.singletonList(Optional.ofNullable(argumentSymbol)),
                Collections.singletonList(BaseValue.Type.BlockType.ordinal()),
                Collections.singletonList(itemTypeValue));
    }
}
