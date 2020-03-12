package prc94.lavafurnace

import net.minecraft.block.Block
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.{IHasContainer, ScreenManager}
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.{BlockItem, Item, ItemGroup}
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.apache.logging.log4j.{LogManager, Logger}
import prc94.lavafurnace.LavaFurnaceMod.LOGGER
import prc94.lavafurnace.RegistryObjects._


object LavaFurnaceMod {
  val LOGGER: Logger = LogManager.getLogger
  val MOD_ID: String = "lavafurnacemod"
}

@Mod("lavafurnacemod") class LavaFurnaceMod {
  FMLJavaModLoadingContext.get.getModEventBus.addListener((_: FMLClientSetupEvent) => this.doClientStuff())
  FMLJavaModLoadingContext.get.getModEventBus.register(new RegistryEvents)
  MinecraftForge.EVENT_BUS.register(this)

  private def doClientStuff(): Unit = {
    LavaFurnaceMod.LOGGER.debug("Doing client stuff")
    ScreenManager.registerFactory(LAVA_FURNACE_CONTAINER, (p: LavaFurnaceContainer, p1: PlayerInventory, p2: ITextComponent) => new LavaFurnaceScreen(p, p1, p2).asInstanceOf[Screen with IHasContainer[LavaFurnaceContainer]])
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) class RegistryEvents() {
    @SubscribeEvent def onBlocksRegistry(blockRegistryEvent: RegistryEvent.Register[Block]): Unit = {
      LOGGER.debug("Registering Blocks")
      blockRegistryEvent.getRegistry.registerAll(LAVA_FURNACE)
    }

    @SubscribeEvent def onItemRegistry(itemRegistryEvent: RegistryEvent.Register[Item]): Unit = {
      LOGGER.debug("Registering Items")
      itemRegistryEvent.getRegistry.registerAll(new BlockItem(LAVA_FURNACE, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(LAVA_FURNACE.getRegistryName))
    }

    @SubscribeEvent def onContainerRegistry(containerRegistryEvent: RegistryEvent.Register[ContainerType[_]]): Unit = {
      LOGGER.debug("Registering ContainerType objects")
      containerRegistryEvent.getRegistry.registerAll(LAVA_FURNACE_CONTAINER.setRegistryName(LavaFurnaceMod.MOD_ID, "lava_furnace"))
    }

    @SubscribeEvent def onTileEntityRegistry(event: RegistryEvent.Register[TileEntityType[_]]): Unit = {
      LOGGER.debug("Registering TileEntityType objects")
      event.getRegistry.registerAll(LAVA_FURNACE_TILEENTITY.setRegistryName(LavaFurnaceMod.MOD_ID, "lava_furnace"))
    }
  }

}
