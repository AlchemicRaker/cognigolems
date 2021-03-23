package com.syntheticentropy.cogni.forge.item;

import com.syntheticentropy.cogni.forge.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class Items {
    public static final Item EXAMPLE_ITEM = new Item(new Item.Properties().tab(ItemGroup.TAB_COGNI)).setRegistryName("example_item");
    public static final Item HIVEMIND = new BlockItem(Blocks.HIVEMIND, new Item.Properties().tab(ItemGroup.TAB_COGNI)).setRegistryName("hivemind");
}
