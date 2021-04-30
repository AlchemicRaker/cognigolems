package com.syntheticentropy.cogni.forge.solution;

import com.syntheticentropy.cogni.forge.entity.ICogniEntity;
import com.syntheticentropy.cogni.forge.entity.goal.MoveToGoal;
import com.syntheticentropy.cogni.forge.symbol.CoordinateValue;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
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

        // if stuck unable to reach a target, abort
        if(creatureEntity.getNavigation().isStuck()) {
            creatureEntity.getNavigation().stop();
            if(entity.getHeldItem().isPresent()) {
                ItemStack heldItem = entity.getHeldItem().get();
                creatureEntity.spawnAtLocation(heldItem); // drop the item
                entity.setHeldItem(ItemStack.EMPTY);
            }
            return true;
        }

        // if there's a held item, time to go put the item
        // reaching the destination and not being able to put it, drop it and abort
        if(!entity.getHeldItem().get().isEmpty()) {
            ItemStack heldItem = entity.getHeldItem().get();
            double dist = creatureEntity.distanceToSqr(toX+0.5, toY+0.5, toZ+0.5);
            if(dist > 1) {
                // need to be able to move
                if(!entity.getMoveToGoal().isPresent()){
                    creatureEntity.spawnAtLocation(heldItem); // drop the item
                    entity.setHeldItem(ItemStack.EMPTY);
                    return true;
                }
                MoveToGoal moveToGoal = entity.getMoveToGoal().get();
                moveToGoal.activate(toX, toY+0.5, toZ, 1);
                return false;
            }

            IItemHandler itemHandler = Optional.ofNullable(creatureEntity.level.getBlockEntity(new BlockPos(toX, toY, toZ)))
                    .map(value -> this.fromSide != null
                            ? value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.fromSide)
                            : value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                    .orElseGet(LazyOptional::empty)
                    .resolve().orElse(null);

            if(itemHandler != null) {
                ItemStack remainingItemStack = injectItem(itemHandler, heldItem, false);

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

        double dist = creatureEntity.distanceToSqr(fromX+0.5, fromY+0.5, fromZ+0.5);
        if(dist > 1) {
            // need to be able to move
            if(!entity.getMoveToGoal().isPresent()){
                return true;
            }
            MoveToGoal moveToGoal = entity.getMoveToGoal().get();
            moveToGoal.activate(fromX, fromY+0.5, fromZ, 1);
            return false;
        }

        // is there a block with an inventory?
        IItemHandler itemHandler = Optional.ofNullable(creatureEntity.level.getBlockEntity(new BlockPos(fromX, fromY, fromZ)))
                .map(value -> this.fromSide != null
                ? value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.fromSide)
                : value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                .orElseGet(LazyOptional::empty)
                .resolve().orElse(null);

        IItemHandler destinationItemHandler = Optional.ofNullable(creatureEntity.level.getBlockEntity(new BlockPos(toX, toY, toZ)))
                .map(value -> this.fromSide != null
                        ? value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.fromSide)
                        : value.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                .orElseGet(LazyOptional::empty)
                .resolve().orElse(null);

        if(itemHandler != null) {
            // only do this if we can verify that the item can be put into the destination
            Integer forceQuantity = null;

            if(destinationItemHandler != null) {
                // known destination inventory? make sure it can fit the item we're about to take
                ItemStack itemsToMove = extractItem(itemHandler, null, true);
                if(!itemsToMove.isEmpty()) {
                    ItemStack predictedRemains = injectItem(destinationItemHandler, itemsToMove, true);
                    if(!predictedRemains.isEmpty()) {
                        // attempting to move it won't move everything we can take

                        // if we weren't able to move _anything_, abort now
                        if(itemsToMove.getCount() == predictedRemains.getCount()) {
                            return true;
                        }

                        // able to move some. only take that amount.
                        forceQuantity = itemsToMove.getCount() - predictedRemains.getCount();
                    }
                }
            }

            ItemStack takenItem = extractItem(itemHandler, forceQuantity, false);
            if(takenItem == null) {
                return true; //unable to take item
            }
            return !entity.setHeldItem(takenItem); // if able to set item, more to do next tick!
        }

        //TODO: or is there an entityItem?
//        ItemEntity
//        creatureEntity.level.g(ItemEntity.class,)


        // Nothing to take or pick up, abort
        return true;
    }

    // returns remaining held
    ItemStack injectItem(IItemHandler itemHandler, ItemStack itemStack, boolean simulate) {
        if(toSlot != null) {
            return itemHandler.insertItem(toSlot, itemStack, simulate);
        }
        ItemStack remainingItemStack = itemStack;
        for (int i = 0; i < itemHandler.getSlots() && !remainingItemStack.isEmpty(); i++) {
            remainingItemStack = itemHandler.insertItem(i, remainingItemStack, simulate);
        }
        return remainingItemStack;
    }

    ItemStack extractItem(IItemHandler itemHandler, Integer forceQuantity, boolean simulate) {
        if(fromSlot != null) {
            ItemStack itemStack = itemHandler.getStackInSlot(fromSlot);
            if(!itemStack.isEmpty()) {
                return itemHandler.extractItem(fromSlot, forceQuantity == null ? itemStack.getCount() : Math.min(itemStack.getCount(), forceQuantity), simulate);
            }
            return ItemStack.EMPTY;
        }
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.getStackInSlot(i);
            if(!itemStack.isEmpty()) {
                return itemHandler.extractItem(i, forceQuantity == null ? itemStack.getCount() : Math.min(itemStack.getCount(), forceQuantity), simulate);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        super.load(compoundNBT);

        this.fromX = compoundNBT.contains("fx") ? compoundNBT.getDouble("fx") : 0;
        this.fromY = compoundNBT.contains("fy") ? compoundNBT.getDouble("fy") : 0;
        this.fromZ = compoundNBT.contains("fz") ? compoundNBT.getDouble("fz") : 0;
        this.fromSide = compoundNBT.contains("fside") ? Direction.valueOf(compoundNBT.getString("fside")) : null;
        this.fromSlot = compoundNBT.contains("fslot") ? compoundNBT.getInt("fslot") : null;

        this.toX = compoundNBT.contains("tx") ? compoundNBT.getDouble("tx") : 0;
        this.toY = compoundNBT.contains("ty") ? compoundNBT.getDouble("ty") : 0;
        this.toZ = compoundNBT.contains("tz") ? compoundNBT.getDouble("tz") : 0;
        this.toSide = compoundNBT.contains("tside") ? Direction.valueOf(compoundNBT.getString("tside")) : null;
        this.toSlot = compoundNBT.contains("tslot") ? compoundNBT.getInt("tslot") : null;
    }

    @Override
    public boolean save(CompoundNBT compoundNBT) {
        boolean b = super.save(compoundNBT);

        compoundNBT.putDouble("fx", this.fromX);
        compoundNBT.putDouble("fy", this.fromY);
        compoundNBT.putDouble("fz", this.fromZ);
        if(this.fromSide != null)
            compoundNBT.putString("fside", this.fromSide.name());
        if(this.fromSlot != null)
            compoundNBT.putInt("fslot", this.fromSlot);

        compoundNBT.putDouble("tx", this.toX);
        compoundNBT.putDouble("ty", this.toY);
        compoundNBT.putDouble("tz", this.toZ);
        if(this.toSide != null)
            compoundNBT.putString("tside", this.toSide.name());
        if(this.toSlot != null)
            compoundNBT.putInt("tslot", this.toSlot);
        return b;
    }
}
