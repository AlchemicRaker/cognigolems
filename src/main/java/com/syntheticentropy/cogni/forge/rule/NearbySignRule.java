package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.RuleImplementation;
import com.syntheticentropy.cogni.cognilog.RuleIterator;
import com.syntheticentropy.cogni.cognilog.RuleLine;
import com.syntheticentropy.cogni.cognilog.Symbol;
import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.BaseValue;
import com.syntheticentropy.cogni.forge.symbol.BlockTypeValue;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;
import com.syntheticentropy.cogni.forge.symbol.TextValue;
import net.minecraft.block.*;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NearbySignRule extends RuleLine<Solution> {
    public NearbySignRule(ICogniEntity entity, Integer textSymbol, Integer signCoordinateSymbol, Integer blockCoordinateSymbol) {
        this(entity, Arrays.asList(Optional.ofNullable(textSymbol), Optional.ofNullable(signCoordinateSymbol), Optional.ofNullable(blockCoordinateSymbol)),
                Arrays.asList(BaseValue.Type.Text.ordinal(), BaseValue.Type.Coordinate.ordinal(), BaseValue.Type.Coordinate.ordinal()));
    }
    public NearbySignRule(ICogniEntity entity, List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes) {
        super(argumentSymbols, argumentTypes, Arrays.asList(
                new NearbySignSearchImplementation(entity, argumentSymbols)));
    }

    public static class NearbySignSearchImplementation extends RuleImplementation<Solution> {
        private final List<Optional<Integer>> argumentSymbols;
        private final ICogniEntity entity;

        public NearbySignSearchImplementation(ICogniEntity entity, List<Optional<Integer>> argumentSymbols) {
            this.entity = entity;
            this.argumentSymbols = argumentSymbols;
        }

        @Override
        public int complexity() {
            return 95; //searching nearby signs is SLOW but we plan to have caching improvements, right?
        }

        @Override
        public List<Integer> requiredArgumentIndexes() {
            return Collections.emptyList();
        }

        @Override
        public RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols) {
            if(argumentSymbols.stream().noneMatch(Optional::isPresent)) {
                // no symbol bound to the output, no need to do anything
                return new EmptyRuleIterator();
            }

            if(argumentSymbols.get(0).isPresent()&&!symbols.containsKey(argumentSymbols.get(0).get())) {
                symbols.put(argumentSymbols.get(0).get(), new Symbol<TextValue>(argumentSymbols.get(0).get()));
            }
            if(argumentSymbols.get(1).isPresent()&&!symbols.containsKey(argumentSymbols.get(1).get())) {
                symbols.put(argumentSymbols.get(1).get(), new Symbol<CoordinateValue>(argumentSymbols.get(1).get()));
            }
            if(argumentSymbols.get(2).isPresent()&&!symbols.containsKey(argumentSymbols.get(2).get())) {
                symbols.put(argumentSymbols.get(2).get(), new Symbol<CoordinateValue>(argumentSymbols.get(2).get()));
            }

            Symbol<Object> arg0 = (Symbol<Object>) (argumentSymbols.get(0).isPresent() ? symbols.get(argumentSymbols.get(0).get()) : new Symbol<TextValue>(-1));
            Symbol<Object> arg1 = (Symbol<Object>) (argumentSymbols.get(1).isPresent() ? symbols.get(argumentSymbols.get(1).get()) : new Symbol<CoordinateValue>(-1));
            Symbol<Object> arg2 = (Symbol<Object>) (argumentSymbols.get(2).isPresent() ? symbols.get(argumentSymbols.get(2).get()) : new Symbol<CoordinateValue>(-1));

            Symbol<TextValue> textArgumentSymbol = (Symbol<TextValue>) (Symbol<?>) arg0;
            Symbol<CoordinateValue> signCoordinateArgumentSymbol = (Symbol<CoordinateValue>) (Symbol<?>) arg1;
            Symbol<CoordinateValue> blockCoordinateArgumentSymbol = (Symbol<CoordinateValue>) (Symbol<?>) arg2;

//            Optional<TextValue> filterTextCoordinateValue = textArgumentSymbol.getValue();
//            Optional<CoordinateValue> filterSignCoordinateValue = signCoordinateArgumentSymbol.getValue();
//            Optional<CoordinateValue> filterBlockCoordinateValue = blockCoordinateArgumentSymbol.getValue();

            List<SignResult> signResults = findSignResults(textArgumentSymbol, signCoordinateArgumentSymbol, blockCoordinateArgumentSymbol);
            // create an iterator that returns these

            return new RuleIterator(Arrays.asList(Optional.of(arg0), Optional.of(arg1), Optional.of(arg2))) {
                private int counter = 0;
                @Override
                public RuleIteratorResult next(int limit) {
                    if(counter < signResults.size()) {
                        SignResult signResult = signResults.get(counter);
                        this.symbols.get(0).get().setValue(signResult.getText());
                        this.symbols.get(1).get().setValue(signResult.getSignCoordinate());
                        this.symbols.get(2).get().setValue(signResult.getBlockCoordinate());
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
//        private static final List<Block> wallSignBlocks = Arrays.asList(
//                Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN,
//                Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN);
//        private static final List<Block> signBlocks = Arrays.asList(
//                Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN,
//                Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN);

        protected List<SignResult> findSignResults(Symbol<TextValue> textArgumentSymbol, Symbol<CoordinateValue> signCoordinateArgumentSymbol, Symbol<CoordinateValue> blockCoordinateArgumentSymbol) {
            Optional<TextValue> filterTextCoordinateValue = textArgumentSymbol.getValue();
            Optional<CoordinateValue> filterSignCoordinateValue = signCoordinateArgumentSymbol.getValue();
            Optional<CoordinateValue> filterBlockCoordinateValue = blockCoordinateArgumentSymbol.getValue();

            int i = this.searchRange;
            int j = this.verticalSearchRange;
            BlockPos blockpos = this.entity.getCreatureEntity().get().blockPosition();
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            List<SignResult> signResults = new ArrayList<>();
            World world = this.entity.getCreatureEntity().get().level;

            for(int k = this.verticalSearchStart; k <= j; k = k > 0 ? -k : 1 - k) {
                for(int l = 0; l < i; ++l) {
                    for(int i1 = 0; i1 <= l; i1 = i1 > 0 ? -i1 : 1 - i1) {
                        for(int j1 = i1 < l && i1 > -l ? l : 0; j1 <= l; j1 = j1 > 0 ? -j1 : 1 - j1) {
                            blockpos$mutable.setWithOffset(blockpos, i1, k - 1, j1);

                            if(filterSignCoordinateValue.isPresent()) {
                                CoordinateValue filterSignCoordinate = filterSignCoordinateValue.get();
                                if(blockpos$mutable.getX() != filterSignCoordinate.getX() ||
                                        blockpos$mutable.getY() != filterSignCoordinate.getY() ||
                                        blockpos$mutable.getZ() != filterSignCoordinate.getZ()) {
                                    continue;
                                }
                            }

                            BlockState blockState = world.getBlockState(blockpos$mutable);
                            Block block = blockState.getBlock();
                            if (block instanceof WallSignBlock) {
                                Direction direction = blockState.getValue(HorizontalBlock.FACING);
                                BlockPos hostPos = blockpos$mutable.relative(direction.getOpposite());

                                if(filterBlockCoordinateValue.isPresent()) {
                                    CoordinateValue filterBlockCoordinate = filterBlockCoordinateValue.get();
                                    if(hostPos.getX() != filterBlockCoordinate.getX() ||
                                            hostPos.getY() != filterBlockCoordinate.getY() ||
                                            hostPos.getZ() != filterBlockCoordinate.getZ()) {
                                        continue;
                                    }
                                }
                                TileEntity tileEntity = entity.getCreatureEntity().get().level.getBlockEntity(blockpos$mutable);
                                if(!(tileEntity instanceof SignTileEntity)) {
                                    continue;
                                }
                                SignTileEntity signTileEntity = (SignTileEntity) tileEntity;


                                List<TextValue> textValues = Stream.of(
                                        signTileEntity.getMessage(0).getContents(),
                                        signTileEntity.getMessage(1).getContents(),
                                        signTileEntity.getMessage(2).getContents(),
                                        signTileEntity.getMessage(3).getContents()
                                ).filter(text -> {
                                    if(text.length() == 0) return false;
                                    return !filterTextCoordinateValue.map(ft -> !ft.getText().equals(text)).orElse(false);
                                }).map(TextValue::new).collect(Collectors.toList());

                                textValues.forEach(tv -> {
                                    signResults.add(new SignResult(tv, new CoordinateValue(blockpos$mutable), new CoordinateValue(hostPos, direction)));
                                });

                                if(signResults.size() > 100) {
                                    return signResults;
                                }
                            } else if (block instanceof StandingSignBlock) {
                                BlockPos hostPos = blockpos$mutable.below();

                                if(filterBlockCoordinateValue.isPresent()) {
                                    CoordinateValue filterBlockCoordinate = filterBlockCoordinateValue.get();
                                    if(hostPos.getX() != filterBlockCoordinate.getX() ||
                                            hostPos.getY() != filterBlockCoordinate.getY() ||
                                            hostPos.getZ() != filterBlockCoordinate.getZ()) {
                                        continue;
                                    }
                                }
                                TileEntity tileEntity = entity.getCreatureEntity().get().level.getBlockEntity(blockpos$mutable);
                                if(!(tileEntity instanceof SignTileEntity)) {
                                    continue;
                                }
                                SignTileEntity signTileEntity = (SignTileEntity) tileEntity;

                                List<TextValue> textValues = Stream.of(
                                        signTileEntity.getMessage(0).getContents(),
                                        signTileEntity.getMessage(1).getContents(),
                                        signTileEntity.getMessage(2).getContents(),
                                        signTileEntity.getMessage(3).getContents()
                                        ).filter(text -> {
                                            if(text.length() == 0) return false;
                                            return !filterTextCoordinateValue.map(ft -> !ft.getText().equals(text)).orElse(false);
                                        }).map(TextValue::new).collect(Collectors.toList());

                                textValues.forEach(tv -> {
                                    signResults.add(new SignResult(tv, new CoordinateValue(blockpos$mutable), new CoordinateValue(hostPos)));
                                });

                                if(signResults.size() > 100) {
                                    return signResults;
                                }
                            }
                        }
                    }
                }
            }

            return signResults;
        }

        public static class SignResult {
            private final TextValue text;
            private final CoordinateValue signCoordinate;
            private final CoordinateValue blockCoordinate;
            public SignResult(TextValue text, CoordinateValue signCoordinate, CoordinateValue blockCoordinate) {
                this.text = text;
                this.signCoordinate = signCoordinate;
                this.blockCoordinate = blockCoordinate;
            }

            public TextValue getText() {
                return text;
            }

            public CoordinateValue getSignCoordinate() {
                return signCoordinate;
            }

            public CoordinateValue getBlockCoordinate() {
                return blockCoordinate;
            }
        }
    }
}
