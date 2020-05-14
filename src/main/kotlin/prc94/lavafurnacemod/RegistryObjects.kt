package prc94.lavafurnacemod

import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.extensions.IForgeContainerType
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceBlock
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceContainer
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceTileEntity
import java.util.function.Supplier

const val name = "lava_furnace"
val registryName = ResourceLocation(MOD_ID, name)

val LAVA_FURNACE: Block = LavaFurnaceBlock()
        .setRegistryName(registryName)

@Suppress("UNCHECKED_CAST")
val LAVA_FURNACE_CONTAINER: ContainerType<LavaFurnaceContainer> =
        IForgeContainerType.create { windowId: Int, inv: PlayerInventory, _: PacketBuffer? -> LavaFurnaceContainer(windowId, inv) }
        .setRegistryName(registryName) as ContainerType<LavaFurnaceContainer>

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
val LAVA_FURNACE_TILEENTITY: TileEntityType<*> = TileEntityType.Builder
        .create(Supplier(::LavaFurnaceTileEntity), LAVA_FURNACE)
        .build(null)
        .setRegistryName(registryName)
