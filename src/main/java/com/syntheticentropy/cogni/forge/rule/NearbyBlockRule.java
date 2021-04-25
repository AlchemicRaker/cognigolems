package com.syntheticentropy.cogni.forge.rule;

import com.google.common.collect.Lists;
import com.syntheticentropy.cogni.cognilog.RuleImplementation;
import com.syntheticentropy.cogni.cognilog.RuleIterator;
import com.syntheticentropy.cogni.cognilog.RuleLine;
import com.syntheticentropy.cogni.cognilog.Symbol;
import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.BlockTypeValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Stream;

public class NearbyBlockRule extends RuleLine<Solution> {
    public NearbyBlockRule(ICogniEntity entity, Integer blockTypeSymbol, Integer coordinateSymbol) {
        this(entity, Arrays.asList(Optional.of(blockTypeSymbol), Optional.of(coordinateSymbol)), Arrays.asList(BaseValue.Type.BlockType.ordinal(), BaseValue.Type.Coordinate.ordinal()));
    }
    public NearbyBlockRule(ICogniEntity entity, List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes) {
        super(argumentSymbols, argumentTypes, Arrays.asList(
                new NearbyBlockTypeToCoordinatesImplementation(entity, argumentSymbols)));
    }
    // NearbyBlockRule([Block Type], Coordinate)
    // TODO: NearbyBlockRule(Block Type, [Coordinate])

    public static class NearbyBlockTypeToCoordinatesImplementation extends RuleImplementation<Solution> {
        private final List<Optional<Integer>> argumentSymbols;
        private final ICogniEntity entity;

        public NearbyBlockTypeToCoordinatesImplementation(ICogniEntity entity, List<Optional<Integer>> argumentSymbols) {
            this.entity = entity;
            this.argumentSymbols = argumentSymbols;
        }

        @Override
        public int complexity() {
            return 100; //searching nearby blocks is SLOW
        }

        @Override
        public List<Integer> requiredArgumentIndexes() {
            return Arrays.asList(0);
        }

        @Override
        public RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols) {
            Symbol<Object> arg0 = (Symbol<Object>) symbols.get(argumentSymbols.get(0).get());
            Symbol<BlockTypeValue> blockArgumentSymbol = (Symbol<BlockTypeValue>) symbols.get(argumentSymbols.get(0).get());
            BlockTypeValue blockTypeValue = blockArgumentSymbol.getValue().get();
            if(!argumentSymbols.get(1).isPresent()) {
                // no symbol bound to the output, no need to do anything
                return new EmptyRuleIterator();
            }
            if(!symbols.containsKey(argumentSymbols.get(1).get())) {
                symbols.put(argumentSymbols.get(1).get(), new Symbol<CoordinateValue>(argumentSymbols.get(1).get()));
            }
            Symbol<Object> arg1 = (Symbol<Object>) symbols.get(argumentSymbols.get(1).get());
            Symbol<CoordinateValue> coordArgumentSymbol = (Symbol<CoordinateValue>) symbols.get(argumentSymbols.get(1).get());
            Optional<CoordinateValue> filterCoordinateValue = coordArgumentSymbol.getValue();

            List<CoordinateValue> coordinateValues = findBlockCoordinates(blockTypeValue.getBlockType());

            return new RuleIterator(Arrays.asList(Optional.of(arg0), Optional.of(arg1))) {
                private int counter = 0;
                @Override
                public RuleIteratorResult next(int limit) {
                    while(counter < coordinateValues.size()) {
                        CoordinateValue nextCoordinateValue = coordinateValues.get(counter);
                        if(filterCoordinateValue.isPresent() &&
                                !filterCoordinateValue.get().equalsValue(nextCoordinateValue)) {
                            counter++;
                            continue;
                        }
                        this.symbols.get(1).get().setValue(nextCoordinateValue);
                        counter++;
                        return new RuleIteratorResult(true, false, 1);
                    }
                    return new RuleIteratorResult(false, true, 1);
                }
            };
        }

        int searchRange = 10;
        int verticalSearchRange = 7;
        int verticalSearchStart = 0;

        protected List<CoordinateValue> findBlockCoordinates(Block blockType) {
            int i = this.searchRange;
            int j = this.verticalSearchRange;
            BlockPos blockpos = this.entity.getCreatureEntity().get().blockPosition();
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            List<CoordinateValue> coordinateValues = new ArrayList<>();
            World world = this.entity.getCreatureEntity().get().level;

            for(int k = this.verticalSearchStart; k <= j; k = k > 0 ? -k : 1 - k) {
                for(int l = 0; l < i; ++l) {
                    for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                        for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                            blockpos$mutable.setWithOffset(blockpos, i1, k - 1, j1);
                            Block block = world.getBlockState(blockpos$mutable).getBlock();
                            if(block == blockType) {
                                coordinateValues.add(new CoordinateValue(blockpos$mutable.getX(),blockpos$mutable.getY(),blockpos$mutable.getZ()));
                                if(coordinateValues.size() > 100) {
                                    return coordinateValues;
                                }
                            }
                        }
                    }
                }
            }

            return coordinateValues;
        }
    }

    public static class NearbyBlockCoordinateToTypeImplementation extends RuleImplementation<Solution> {
        private final List<Optional<Integer>> argumentSymbols;
        private final ICogniEntity entity;

        public NearbyBlockCoordinateToTypeImplementation(ICogniEntity entity, List<Optional<Integer>> argumentSymbols) {
            this.entity = entity;
            this.argumentSymbols = argumentSymbols;
        }

        @Override
        public int complexity() {
            return 10; // Very low
        }

        @Override
        public List<Integer> requiredArgumentIndexes() {
            return Arrays.asList(1);
        }

        @Override
        public RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols) {
            if(!argumentSymbols.get(0).isPresent()) {
                // no symbol bound to the output, no need to do anything
                return new EmptyRuleIterator();
            }
            if(!symbols.containsKey(argumentSymbols.get(0).get())) {
                symbols.put(argumentSymbols.get(0).get(), new Symbol<BlockTypeValue>(argumentSymbols.get(0).get()));
            }
            Symbol<Object> arg0 = (Symbol<Object>) symbols.get(argumentSymbols.get(0).get());
            Symbol<BlockTypeValue> blockArgumentSymbol = (Symbol<BlockTypeValue>) symbols.get(argumentSymbols.get(0).get());
            BlockTypeValue blockTypeValue = blockArgumentSymbol.getValue().get();

            Symbol<Object> arg1 = (Symbol<Object>) symbols.get(argumentSymbols.get(1).get());
            Symbol<CoordinateValue> coordArgumentSymbol = (Symbol<CoordinateValue>) symbols.get(argumentSymbols.get(1).get());

            return new RuleIterator(Arrays.asList(Optional.of(arg0), Optional.of(arg1))) {
                boolean firstRun = true;
                @Override
                public RuleIteratorResult next(int limit) {
                    if(firstRun){
                        firstRun = false;
                    }
                    return new RuleIteratorResult(false, true, 1);
                }
            };
        }
    }
}
