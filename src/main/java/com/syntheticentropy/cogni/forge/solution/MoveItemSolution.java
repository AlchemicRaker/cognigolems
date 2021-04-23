package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import com.syntheticentropy.cogni.forge.entity.goal.MoveToGoal;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Optional;

public class MoveItemSolution extends Solution {

    protected double fromX;
    protected double fromY;
    protected double fromZ;
    protected Direction fromSide;
    protected Integer fromSlot;

    protected double toX;
    protected double toY;
    protected double toZ;
    protected Direction toSide;
    protected Integer toSlot;

    public MoveItemSolution() {
        super("moveItem");
    }

    public MoveItemSolution(CoordinateValue fromCoordinate, CoordinateValue toCoordinate) {
        this();

        this.fromX = fromCoordinate.getX();
        this.fromY = fromCoordinate.getY();
        this.fromZ = fromCoordinate.getZ();
        this.fromSide = fromCoordinate.getSide();
        this.fromSlot = fromCoordinate.getSlot();

        this.toX = toCoordinate.getX();
        this.toY = toCoordinate.getY();
        this.toZ = toCoordinate.getZ();
        this.toSide = toCoordinate.getSide();
        this.toSlot = toCoordinate.getSlot();
    }

    public boolean ableToMove(ICogniEntity entity) {
        return entity.getMoveToGoal().isPresent() && entity.getCreatureEntity().isPresent();
    }
    @Override
    public boolean tick(ICogniEntity entity) {
        if(entity.getMoveToGoal().isPresent() && !entity.getMoveToGoal().get().isDone()) return false; // already moving

        // unable to hold an item? not a creature? nothing to do here!
        if(!entity.getHeldItem().isPresent() || !entity.getCreatureEntity().isPresent()) return true;

        CreatureEntity creatureEntity = entity.getCreatureEntity().get();

        // if there's a held item, time to go put the item
        // reaching the destination and not being able to put it, drop it and abort
        if(entity.getHeldItem().get().isEmpty()) {
            ItemStack heldItem = entity.getHeldItem().get();
            double dist = creatureEntity.distanceToSqr(toX, toY, toZ);
            if(dist > 1) {
                // need to be able to move
                if(!entity.getMoveToGoal().isPresent()){
                    creatureEntity.spawnAtLocation(heldItem); // drop the item
                    entity.setHeldItem(ItemStack.EMPTY);
                    return true;
                }
                MoveToGoal moveToGoal = entity.getMoveToGoal().get();
                moveToGoal.activate(toX, toY, toZ, 1);
                return false;
            }
//            ChickenEntity
            //TODO: put item into that inventory

            IItemHandler itemHandler = Optional.ofNullable(creatureEntity.level.getBlockEntity(new BlockPos(fromX, fromY, fromZ)))
                    .map(value -> this.fromSide != null
                            ? value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.fromSide)
                            : value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                    .orElseGet(LazyOptional::empty)
                    .resolve().orElse(null);

            if(itemHandler != null) {
                ItemStack remainingItemStack = injectItem(itemHandler, heldItem);

                // drop whatever doesn't fit in the destination
                if(!remainingItemStack.isEmpty()) {
                    creatureEntity.spawnAtLocation(remainingItemStack);
                }

                entity.setHeldItem(ItemStack.EMPTY);

                return true; //either way, all done
            }

            // no target inventory, drop at destination
            creatureEntity.spawnAtLocation(heldItem); // drop the item
            entity.setHeldItem(ItemStack.EMPTY);
            return true;
        }

        // if there's no held item, time to go get an item
        // reaching the destination and not getting the item aborts

        double dist = creatureEntity.distanceToSqr(fromX, fromY, fromZ);
        if(dist > 1) {
            // need to be able to move
            if(!entity.getMoveToGoal().isPresent()){
                return true;
            }
            MoveToGoal moveToGoal = entity.getMoveToGoal().get();
            moveToGoal.activate(fromX, fromY, fromZ, 1);
            return false;
        }

        // is there a block with an inventory?
        IItemHandler itemHandler = Optional.ofNullable(creatureEntity.level.getBlockEntity(new BlockPos(fromX, fromY, fromZ)))
                .map(value -> this.fromSide != null
                ? value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.fromSide)
                : value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                .orElseGet(LazyOptional::empty)
                .resolve().orElse(null);

        if(itemHandler != null) {
            ItemStack takenItem = extractItem(itemHandler);
            if(takenItem == null) {
                return true; //unable to take item
            }
            return !entity.setHeldItem(takenItem); // if able to set item, more to do next tick!
        }

        //TODO: or is there an entityItem?
//        ItemEntity
//        creatureEntity.level.g(ItemEntity.class,)

        return false;
    }

    // returns remaining held
    ItemStack injectItem(IItemHandler itemHandler, ItemStack itemStack) {
        if(toSlot != null) {
            return itemHandler.insertItem(toSlot, itemStack, false);
        }
        ItemStack remainingItemStack = itemStack;
        for (int i = 0; i < itemHandler.getSlots() && !remainingItemStack.isEmpty(); i++) {
            remainingItemStack = itemHandler.insertItem(i, remainingItemStack, false);
        }
        return remainingItemStack;
    }

    ItemStack extractItem(IItemHandler itemHandler) {
        if(fromSlot != null) {
            ItemStack itemStack = itemHandler.getStackInSlot(fromSlot);
            if(!itemStack.isEmpty()) {
                return itemHandler.extractItem(fromSlot, itemStack.getCount(), false);
            }
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if(!itemStack.isEmpty()) {
                return itemHandler.extractItem(i, itemStack.getCount(), false);
            }
        }
        return ItemStack.EMPTY;
    }
}
