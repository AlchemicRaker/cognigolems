package com.syntheticentropy.cogni.forge.action;

import com.syntheticentropy.cogni.cognilog.ActionLine;
import com.syntheticentropy.cogni.cognilog.Symbol;
import com.syntheticentropy.cogni.forge.solution.MoveToCoordSolution;
import com.syntheticentropy.cogni.forge.solution.SampleSolution;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MoveToCoordAction extends ActionLine<Solution> {
    public MoveToCoordAction(Integer coordSymbol) {
        super(Collections.singletonList(coordSymbol), Collections.singletonList(BaseValue.Type.Coordinate.ordinal()));
    }

    @Override
    public Solution createResult(List<Symbol<?>> symbols) {
        assert symbols.size() == 1;
        Symbol<?> arg0 = symbols.get(0);
        assert arg0.getValue().isPresent();
        Optional<CoordinateValue> coordinateValue = arg0.getValue()
                .map(v -> (BaseValue)v).map(BaseValue::getCoordinateValue);
        assert coordinateValue.isPresent();
        return new MoveToCoordSolution(coordinateValue.get());
    }
}
