package com.syntheticentropy.cogni.forge.entity;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLPlayMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@Mod.EventBusSubscriber(modid = "cogni", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityTypes  {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final EntityType<CognigolemEntity> COGNIGOLEM = makeType("cognigolem", EntityType.Builder.<CognigolemEntity>of(CognigolemEntity::new, EntityClassification.MISC).sized(0.5185F, 1F));

    private static <T extends Entity> EntityType<T> makeType(String name, EntityType.Builder<T> builder) {
        EntityType<T> tmpType = builder.build(name);
        tmpType.setRegistryName(name);
        return tmpType;
    }

    @SubscribeEvent
    public static void entityAttributeCreate(EntityAttributeCreationEvent event) {
        event.put(COGNIGOLEM, CognigolemEntity.createAttributes().build());

        LOGGER.info("HELLO from entityAttributeCreate!");
    }
}
