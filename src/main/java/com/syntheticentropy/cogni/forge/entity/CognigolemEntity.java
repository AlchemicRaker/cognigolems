package com.syntheticentropy.cogni.forge.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.OptionalInt;

public class CognigolemEntity extends GolemEntity implements IInventory {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    @Nullable
    private PlayerEntity interactingPlayer;

    protected CognigolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 15.0D);
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        super.load(compoundNBT);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compoundNBT, this.items);
    }

    @Override
    public boolean save(CompoundNBT compoundNBT) {
        boolean b = super.save(compoundNBT);
        ItemStackHelper.saveAllItems(compoundNBT, this.items);
        return b;
    }

    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    protected void setItems(NonNullList<ItemStack> p_199721_1_) {
        this.items = p_199721_1_;
    }

    @Override
    public ItemStack getItem(int p_70301_1_) {
        return this.getItems().get(p_70301_1_);
    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
        ItemStack itemstack = ItemStackHelper.removeItem(this.getItems(), p_70298_1_, p_70298_2_);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_) {
        return ItemStackHelper.takeItem(this.getItems(), p_70304_1_);
    }

    @Override
    public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
        this.getItems().set(p_70299_1_, p_70299_2_);
        if (p_70299_2_.getCount() > this.getMaxStackSize()) {
            p_70299_2_.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public void setChanged() {
        // not useful for mob entities?
    }

    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        if (!this.isAlive()) {
            return false;
        } else {
            return !(playerEntity.distanceToSqr((double)this.getX(), (double)this.getY(), (double)this.getZ()) > 64.0D);
        }
    }


    public ActionResultType mobInteract(PlayerEntity playerEntity, Hand hand) {
//        ItemStack itemstack = playerEntity.getItemInHand(hand);
        if (this.isAlive() /*&& !this.isTrading()*/ && !this.isBaby()) {


            if (!this.level.isClientSide) {
                this.setInteractingPlayer(playerEntity);
                this.openContainer(playerEntity, this.getDisplayName(), 1);
            }

            return ActionResultType.sidedSuccess(this.level.isClientSide);

        } else {
            return super.mobInteract(playerEntity, hand);
        }
    }

    public void setInteractingPlayer(@Nullable PlayerEntity playerEntity) {
        this.interactingPlayer = playerEntity;
    }


    void openContainer(PlayerEntity playerEntity, ITextComponent iTextComponent, int p_213707_3_) {
        OptionalInt optionalint = playerEntity.openMenu(new SimpleNamedContainerProvider((num, playerInventory, playerEntity1) -> {
            return ChestContainer.threeRows(num, playerInventory, this);
        }, iTextComponent));
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }


    protected void dropCustomDeathLoot(DamageSource p_213333_1_, int p_213333_2_, boolean p_213333_3_) {
        super.dropCustomDeathLoot(p_213333_1_, p_213333_2_, p_213333_3_);
        InventoryHelper.dropContents(this.level, this, this);
    }

}
