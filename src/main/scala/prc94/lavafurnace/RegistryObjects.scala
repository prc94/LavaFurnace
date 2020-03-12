package prc94.lavafurnace

import net.minecraft.block.Block
import net.minecraft.block.material.{Material, MaterialColor}
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.extensions.IForgeContainerType

object RegistryObjects {
  val LAVA_FURNACE: Block = new LavaFurnaceBlock(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(50.0F, 1200.0F)).setRegistryName(LavaFurnaceMod.MOD_ID, "lava_furnace")

  val LAVA_FURNACE_CONTAINER: ContainerType[LavaFurnaceContainer] = IForgeContainerType.create((windowId: Int, inv: PlayerInventory, _: PacketBuffer) => new LavaFurnaceContainer(windowId, inv))

  val LAVA_FURNACE_TILEENTITY: TileEntityType[LavaFurnaceTileEntity] = TileEntityType.Builder.create(() => new LavaFurnaceTileEntity, LAVA_FURNACE).build(null)
}
