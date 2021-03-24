package com.syntheticentropy.cogni.forge.tileentity;

import com.mojang.datafixers.types.Type;
import com.syntheticentropy.cogni.forge.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityTypes {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final TileEntityType<HivemindTileEntity> HIVEMIND = makeType("hivemind", TileEntityType.Builder.of(HivemindTileEntity::new, Blocks.HIVEMIND));

    private static <T extends TileEntity> TileEntityType<T> makeType(String name, TileEntityType.Builder<T> builder) {
        Type<?> type = Util.fetchChoiceType(TypeReferences.BLOCK_ENTITY, name);
        TileEntityType<T> tmpTileEntityType = builder.build(type);
        tmpTileEntityType.setRegistryName(name);
        return tmpTileEntityType;
    }

}
