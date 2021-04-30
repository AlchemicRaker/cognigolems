package com.syntheticentropy.cogni.forge.rule;

import com.syntheticentropy.cogni.cognilog.RuleImplementation;
import com.syntheticentropy.cogni.cognilog.RuleIterator;
import com.syntheticentropy.cogni.cognilog.RuleLine;
import com.syntheticentropy.cogni.cognilog.Symbol;
import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import com.syntheticentropy.cogni.forge.solution.Solution;
import com.syntheticentropy.cogni.forge.symbol.*;
import net.minecraft.block.Block;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

public class InventoryRule extends RuleLine<Solution> {

    public InventoryRule(ICogniEntity entity, Integer inventoryCoordinateSymbol, Integer slotSymbol, Integer itemTypeSymbol) {
        this(entity, Arrays.asList(Optional.ofNullable(inventoryCoordinateSymbol), Optional.ofNullable(slotSymbol), Optional.ofNullable(itemTypeSymbol)),
                Arrays.asList(BaseValue.Type.Coordinate.ordinal(), BaseValue.Type.Coordinate.ordinal(), BaseValue.Type.ItemType.ordinal()));
    }
    public InventoryRule(ICogniEntity entity, List<Optional<Integer>> argumentSymbols, List<Integer> argumentTypes) {
        super(argumentSymbols, argumentTypes, Arrays.asList(
                new InventoryToSlotImplementation(entity, argumentSymbols)));
    }

    public static class InventoryToSlotImplementation extends RuleImplementation<Solution> {
        private final List<Optional<Integer>> argumentSymbols;
        private final ICogniEntity entity;

        public InventoryToSlotImplementation(ICogniEntity entity, List<Optional<Integer>> argumentSymbols) {
            this.argumentSymbols = argumentSymbols;
            this.entity = entity;
        }

        @Override
        public int complexity() {
            return 30; //searching inventories is not too bad
        }

        @Override
        public List<Integer> requiredArgumentIndexes() {
            return Arrays.asList(0);
        }

        @Override
        public RuleIterator createRuleIterator(Map<Integer, Symbol<?>> symbols) {
            Symbol<Object> arg0 = (Symbol<Object>) symbols.get(argumentSymbols.get(0).get());
            Symbol<CoordinateValue> inventoryCoordinateSymbol = (Symbol<CoordinateValue>) symbols.get(argumentSymbols.get(0).get());
            CoordinateValue inventoryCoordinateValue = inventoryCoordinateSymbol.getValue().get();

            // TODO: be able to get inventory from entities
            BlockPos inventoryBlockPos = new BlockPos(inventoryCoordinateValue.getX(), inventoryCoordinateValue.getY(), inventoryCoordinateValue.getZ());
//            if(!argumentSymbols.get(1).isPresent() || !argumentSymbols.get(2).isPresent()) {
//                // no symbol bound to the output, no need to do anything
//                return new EmptyRuleIterator();
//            }

            if(argumentSymbols.get(1).isPresent()&&!symbols.containsKey(argumentSymbols.get(1).get())) {
                symbols.put(argumentSymbols.get(1).get(), new Symbol<CoordinateValue>(argumentSymbols.get(1).get()));
            }
            if(argumentSymbols.get(2).isPresent()&&!symbols.containsKey(argumentSymbols.get(2).get())) {
                symbols.put(argumentSymbols.get(2).get(), new Symbol<ItemTypeValue>(argumentSymbols.get(2).get()));
            }

            Symbol<Object> arg1 = (Symbol<Object>) (argumentSymbols.get(1).isPresent() ? symbols.get(argumentSymbols.get(1).get()) : new Symbol<CoordinateValue>(-1));
            Symbol<Object> arg2 = (Symbol<Object>) (argumentSymbols.get(2).isPresent() ? symbols.get(argumentSymbols.get(2).get()) : new Symbol<CoordinateValue>(-1));

            Symbol<CoordinateValue> slotSymbol = (Symbol<CoordinateValue>) (Symbol<?>) arg1;
            Symbol<ItemTypeValue> itemTypeSymbol = (Symbol<ItemTypeValue>) (Symbol<?>) arg2;

            Optional<CoordinateValue> maybeFilterSlotValue = slotSymbol.getValue();
            if(maybeFilterSlotValue.isPresent() && maybeFilterSlotValue.get().getSlot() == null) {
                maybeFilterSlotValue = Optional.empty();
            }
            Optional<CoordinateValue> filterSlotValue = maybeFilterSlotValue;

//            Symbol<Object> arg2 = (Symbol<Object>) symbols.get(argumentSymbols.get(2).get());
//            Symbol<ItemTypeValue> itemTypeSymbol = (Symbol<ItemTypeValue>) symbols.get(argumentSymbols.get(2).get());
            Optional<ItemTypeValue> filterItemTypeValue = itemTypeSymbol.getValue();

            if(!entity.getCreatureEntity().isPresent()) {
                return new EmptyRuleIterator();
            }
            CreatureEntity creatureEntity = entity.getCreatureEntity().get();

            return new RuleIterator(Arrays.asList(Optional.of(arg0), Optional.of(arg1), Optional.of(arg2))) {
                private int counter = 0;
                @Override
                public RuleIteratorResult next(int limit) {
                    // Find the inventory to work with (or give up immediately)

                    IItemHandler itemHandler = Optional.ofNullable(creatureEntity.level.getBlockEntity(inventoryBlockPos))
                            .map(value -> inventoryCoordinateValue.getSide() != null
                                    ? value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inventoryCoordinateValue.getSide())
                                    : value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                            .orElseGet(LazyOptional::empty)
                            .resolve().orElse(null);

                    if(itemHandler == null) {
                        return new RuleIteratorResult(false, true, 1);
                    }

                    while(counter < itemHandler.getSlots()) {
                        CoordinateValue nextSlotValue = new CoordinateValue(
                                inventoryCoordinateValue.getX(),
                                inventoryCoordinateValue.getY(),
                                inventoryCoordinateValue.getZ(),
                                inventoryCoordinateValue.getSide(),
                                counter);
                        if(filterSlotValue.isPresent() &&
                            !filterSlotValue.get().equalsValue(nextSlotValue)) {
                            counter++;
                            continue;
                        }
                        ItemStack nextItemStack = itemHandler.getStackInSlot(counter);
                        if(nextItemStack.isEmpty()) {
                            counter++;
                            continue;
                        }
                        ItemTypeValue nextItemType = new ItemTypeValue(nextItemStack.getItem());
                        if(filterItemTypeValue.isPresent() &&
                                !filterItemTypeValue.get().equalsValue(nextItemType)) {
                            counter++;
                            continue;
                        }
                        this.symbols.get(1).get().setValue(nextSlotValue);
                        this.symbols.get(2).get().setValue(nextItemType);
                        counter++;
                        return new RuleIteratorResult(true, false, 1);
                    }
                    return new RuleIteratorResult(false, true, 1);
                }
            };
        }
    }
}
