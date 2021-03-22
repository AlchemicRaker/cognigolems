package com.syntheticentropy.cogni.forge.renderer;

import com.syntheticentropy.cogni.forge.entity.CognigolemEntity;
import com.syntheticentropy.cogni.forge.entity.EntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "cogni", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRenderers {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerEntityRenderers(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypes.COGNIGOLEM, CognigolemRenderer::new);

        LOGGER.info("HELLO from registerEntityRenderers!");
    }
}
