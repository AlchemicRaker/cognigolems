package com.syntheticentropy.cogni.forge;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGroup {
    public static final net.minecraft.item.ItemGroup TAB_COGNI = new net.minecraft.item.ItemGroup(2, "cogni") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(Items.EXAMPLE_ITEM);
        }
    };
}
