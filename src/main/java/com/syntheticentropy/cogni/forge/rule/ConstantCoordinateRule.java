package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.*;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConstantCoordinateRule extends ConstantRuleLine<Solution> {
    public ConstantCoordinateRule(Integer argumentSymbol, CoordinateValue coordinateValue) {
        super(Collections.singletonList(Optional.ofNullable(argumentSymbol)),
                Collections.singletonList(BaseValue.Type.Coordinate.ordinal()),
                Collections.singletonList(coordinateValue));
    }
}
