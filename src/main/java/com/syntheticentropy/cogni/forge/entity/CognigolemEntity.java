package com.syntheticentropy.cogni.forge.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

public class CognigolemEntity extends GolemEntity {

    protected CognigolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D).add(Attributes.ATTACK_DAMAGE, 15.0D);
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        super.load(compoundNBT);
    }

    @Override
    public boolean save(CompoundNBT compoundNBT) {
        return super.save(compoundNBT);
    }
}
