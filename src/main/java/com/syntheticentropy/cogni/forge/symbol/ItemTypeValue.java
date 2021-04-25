package com.syntheticentropy.cogni.forge.symbol;

import com.syntheticentropy.cogni.forge.item.Items;
import net.minecraft.item.Item;

public class ItemTypeValue extends BaseValue {
    private final Item itemType;
    public ItemTypeValue(Item itemType) {
        super(Type.ItemType);
        this.itemType = itemType;
    }

    public Item getItemType() {
        return itemType;
    }

    public boolean equalsValue(BaseValue b) {
        ItemTypeValue ba = this;
        if(!super.equalsValue(b)) return false;
        ItemTypeValue bb = (ItemTypeValue) b;
        return  ba.itemType == bb.itemType;
    }
}
