package prc94.lavafurnacemod

import net.minecraft.block.Block
import net.minecraft.client.gui.ScreenManager
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceContainer
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceScreen
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT as ModLoadingContext

const val MOD_ID: String = "lavafurnacemod"

@Mod(MOD_ID)
class LavaFurnaceMod {
    init {
        ModLoadingContext.getEventBus().addListener<FMLClientSetupEvent> { doClientStuff() }
        MinecraftForge.EVENT_BUS.register(this)
    }

    private fun doClientStuff() {
        LOGGER.debug("Doing client stuff")
        ScreenManager.registerFactory(LAVA_FURNACE_CONTAINER) { p: LavaFurnaceContainer, p1: PlayerInventory, p2: ITextComponent -> LavaFurnaceScreen(p, p1, p2) }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    companion object {
        val LOGGER: Logger = LogManager.getLogger()

        @SubscribeEvent
        fun onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register<Block>) {
            LOGGER.debug("Registering Blocks")
            blockRegistryEvent.registry.registerAll(LAVA_FURNACE)
        }

        @SubscribeEvent
        fun onItemRegistry(itemRegistryEvent: RegistryEvent.Register<Item>) {
            LOGGER.debug("Registering Items")
            itemRegistryEvent.registry.registerAll(
                    BlockItem(LAVA_FURNACE, Item.Properties()
                            .group(ItemGroup.DECORATIONS))
                            .setRegistryName(LAVA_FURNACE.registryName))
        }

        @SubscribeEvent
        fun onContainerRegistry(containerRegistryEvent: RegistryEvent.Register<ContainerType<*>>) {
            LOGGER.debug("Registering ContainerType objects")
            containerRegistryEvent.registry.registerAll(LAVA_FURNACE_CONTAINER)
        }

        @SubscribeEvent
        fun onTileEntityRegistry(event: RegistryEvent.Register<TileEntityType<*>>) {
            LOGGER.debug("Registering TileEntityType objects")
            event.registry.registerAll(LAVA_FURNACE_TILEENTITY)
        }
    }

}
