package com.syntheticentropy.cogni.forge.entity;

import com.syntheticentropy.cogni.forge.entity.goal.MoveToGoal;
import net.minecraft.entity.CreatureEntity;

import java.util.Optional;

public interface ICogniEntity {
    Optional<MoveToGoal> getMoveToGoal();
    Optional<CreatureEntity> getCreatureEntity();
}
