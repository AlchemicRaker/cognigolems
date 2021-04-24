package com.syntheticentropy.cogni.forge.action;

import com.syntheticentropy.cogni.cognilog.ActionLine;
import com.syntheticentropy.cogni.cognilog.Symbol;
import com.syntheticentropy.cogni.forge.solution.MoveItemSolution;
import com.syntheticentropy.cogni.forge.solution.MoveToCoordSolution;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MoveItemAction extends ActionLine<Solution> {

    public MoveItemAction(Integer fromSymbol, Integer toSymbol) {
        super(Arrays.asList(fromSymbol, toSymbol),
                Arrays.asList(BaseValue.Type.Coordinate.ordinal(), BaseValue.Type.Coordinate.ordinal()));
    }

    @Override
    public Solution createResult(List<Symbol<?>> symbols) {
        assert symbols.size() == 2;
        Symbol<?> arg0 = symbols.get(0);
        Symbol<?> arg1 = symbols.get(1);
        assert arg0.getValue().isPresent() && arg1.getValue().isPresent();
        Optional<CoordinateValue> fromCoordinate = arg0.getValue()
                .map(v -> (BaseValue)v).map(BaseValue::getCoordinateValue);
        Optional<CoordinateValue> toCoordinate = arg1.getValue()
                .map(v -> (BaseValue)v).map(BaseValue::getCoordinateValue);
        assert fromCoordinate.isPresent() && toCoordinate.isPresent();
        return new MoveItemSolution(fromCoordinate.get(), toCoordinate.get());
    }
}
