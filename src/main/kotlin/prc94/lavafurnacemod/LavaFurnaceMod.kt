package prc94.lavafurnacemod

import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceScreen
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT as ModLoadingContext

const val MOD_ID: String = "lavafurnacemod"

@Mod(MOD_ID)
class LavaFurnaceMod {
    init {
        ModLoadingContext.getEventBus().addListener<FMLClientSetupEvent> { doClientStuff() }
    }

    private fun doClientStuff() {
        log.debug("Registering LavaFurnaceScreen at ScreenManager")
        ScreenManager.registerFactory(LAVA_FURNACE_CONTAINER, ::LavaFurnaceScreen)
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    companion object {
        val log: Logger = LogManager.getLogger()

        @SubscribeEvent
        fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
            log.debug("Registering Blocks")
            blockRegistryEvent.registry.registerAll(LAVA_FURNACE)
        }

        @SubscribeEvent
        fun onItemRegistry(itemRegistryEvent: RegistryEvent.Register<Item>) {
            log.debug("Registering Items")
            itemRegistryEvent.registry.registerAll(
                    BlockItem(LAVA_FURNACE, Item.Properties()
                            .group(ItemGroup.DECORATIONS))
                            .setRegistryName(LAVA_FURNACE.registryName))
        }

        @SubscribeEvent
        fun onContainerRegistry(containerRegistryEvent: RegistryEvent.Register<ContainerType<*>>) {
            log.debug("Registering ContainerType objects")
            containerRegistryEvent.registry.registerAll(LAVA_FURNACE_CONTAINER)
        }

        @SubscribeEvent
        fun onTileEntityRegistry(event: RegistryEvent.Register<TileEntityType<*>>) {
            log.debug("Registering TileEntityType objects")
            event.registry.registerAll(LAVA_FURNACE_TILEENTITY)
        }
    }
}