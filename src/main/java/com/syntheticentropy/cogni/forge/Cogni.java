package com.syntheticentropy.cogni.forge;
import com.syntheticentropy.cogni.forge.block.Blocks;
import com.syntheticentropy.cogni.forge.entity.EntityTypes;
import com.syntheticentropy.cogni.forge.item.ItemGroup;
import com.syntheticentropy.cogni.forge.item.Items;
import com.syntheticentropy.cogni.forge.tileentity.TileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("cogni")
public class Cogni
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Cogni() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlocks);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(EntityType.class, this::registerEntityTypes);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::registerTileEntityTypes);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT!");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    private void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                Items.EXAMPLE_ITEM,
                Items.HIVEMIND
                );

        LOGGER.info("HELLO from register items!");
    }

    private void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(Blocks.HIVEMIND);

        LOGGER.info("HELLO from register blocks!");
    }

    private void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().registerAll(EntityTypes.COGNIGOLEM);

        LOGGER.info("HELLO from register entityTypes!");
    }

    private void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(TileEntityTypes.HIVEMIND);

        LOGGER.info("HELLO from register tileEntityTypes!");
    }
}
