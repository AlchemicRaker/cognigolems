package com.syntheticentropy.cogni.forge.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.syntheticentropy.cogni.forge.entity.CognigolemEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCracksLayer;
import net.minecraft.client.renderer.entity.layers.IronGolenFlowerLayer;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CognigolemRenderer extends MobRenderer<CognigolemEntity, CognigolemModel<CognigolemEntity>> {
    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("cogni:textures/entity/cognigolem.png");

    public CognigolemRenderer(EntityRendererManager entityRendererManager) {
        super(entityRendererManager, new CognigolemModel<>(), 0.35F);
        // super(,,shadow);

//        this.addLayer(new IronGolemCracksLayer(this));
//        this.addLayer(new IronGolenFlowerLayer(this));
    }

    public ResourceLocation getTextureLocation(CognigolemEntity p_110775_1_) {
        return GOLEM_LOCATION;
    }

    protected void scale(CognigolemEntity entity, MatrixStack matrixStack, float p_225620_3_) {
        super.scale(entity, matrixStack, p_225620_3_);
        matrixStack.scale(0.35F, 0.35F, 0.35F);
    }

    protected void setupRotations(CognigolemEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
        super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
        if (!((double)p_225621_1_.animationSpeed < 0.01D)) {
            float f = 13.0F;
            float f1 = p_225621_1_.animationPosition - p_225621_1_.animationSpeed * (1.0F - p_225621_5_) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
        }
    }
}
