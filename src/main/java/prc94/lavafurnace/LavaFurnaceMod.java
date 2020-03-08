package prc94.lavafurnace;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static prc94.lavafurnace.RegistryObjects.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LavaFurnaceMod.MOD_ID)
public class LavaFurnaceMod {
    public static final String MOD_ID = "lavafurnacemod";
    public static final Logger LOGGER = LogManager.getLogger();

    public LavaFurnaceMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(LAVA_FURNACE_CONTAINER, LavaFurnaceBlock.LavaFurnaceScreen::new);
    }

    @SuppressWarnings("ConstantConditions")
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            LOGGER.debug("Registering Blocks");
            blockRegistryEvent.getRegistry().registerAll(LAVA_FURNACE);

        }

        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            LOGGER.debug("Registering items");
            itemRegistryEvent.getRegistry().registerAll(
                    new BlockItem(LAVA_FURNACE, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(LAVA_FURNACE.getRegistryName()));
        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> containerRegistryEvent) {
            LOGGER.debug("Registering Container objects");
            containerRegistryEvent.getRegistry().registerAll(LAVA_FURNACE_CONTAINER.setRegistryName(MOD_ID, "lava_furnace"));
        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            LOGGER.debug("Registering TileEntityType objects");
            event.getRegistry().registerAll(LAVA_FURNACE_TILEENTITY.setRegistryName(MOD_ID, "lava_furnace"));
        }

    }
}
