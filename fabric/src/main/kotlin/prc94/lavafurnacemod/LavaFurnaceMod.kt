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
import thedarkcolour.kotlinforforge.forge.MOD_BUS

const val MOD_ID: String = "lavafurnacemod"

@Mod(MOD_ID)
object LavaFurnaceMod {
    val log: Logger = LogManager.getLogger()

    init {
        MOD_BUS.register(this)
    }

    @SubscribeEvent
    fun onBlocksRegistry(event: RegistryEvent.Register<Block>) {
        log.debug("Registering LavaFurnaceBlock")
        event.registry.register(LAVA_FURNACE)
    }

    @SubscribeEvent
    fun onItemRegistry(event: RegistryEvent.Register<Item>) {
        log.debug("Registering Lava Furnace BlockItem")
        event.registry.register(BlockItem(LAVA_FURNACE, Item.Properties()
                .group(ItemGroup.DECORATIONS))
                .setRegistryName(registryName))
    }

    @SubscribeEvent
    fun onContainerRegistry(event: RegistryEvent.Register<ContainerType<*>>) {
        log.debug("Registering LavaFurnaceContainer")
        event.registry.register(LAVA_FURNACE_CONTAINER)
    }

    @SubscribeEvent
    fun onTileEntityRegistry(event: RegistryEvent.Register<TileEntityType<*>>) {
        log.debug("Registering LavaFurnaceTileEntity")
        event.registry.register(LAVA_FURNACE_TILEENTITY)
    }

    @SubscribeEvent
    fun doClientSetup(evt: FMLClientSetupEvent) {
        log.debug("Registering LavaFurnaceScreen at ScreenManager")
        ScreenManager.registerFactory(LAVA_FURNACE_CONTAINER, ::LavaFurnaceScreen)
    }
}