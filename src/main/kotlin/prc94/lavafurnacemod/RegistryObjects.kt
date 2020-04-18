package prc94.lavafurnacemod

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ContainerType
import net.minecraft.network.PacketBuffer
import net.minecraft.tileentity.TileEntityType
import net.minecraftforge.common.ToolType
import net.minecraftforge.common.extensions.IForgeContainerType
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceBlock
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceContainer
import prc94.lavafurnacemod.lavafurnace.LavaFurnaceTileEntity
import java.util.function.Supplier

val LAVA_FURNACE: Block = LavaFurnaceBlock(Block.Properties
        .create(Material.ROCK, MaterialColor.BLACK)
        .hardnessAndResistance(50.0F, 1200.0F)
        .harvestTool(ToolType.PICKAXE)
        .harvestLevel(3)
).setRegistryName(MOD_ID, "lava_furnace")

@Suppress("UNCHECKED_CAST")
val LAVA_FURNACE_CONTAINER: ContainerType<LavaFurnaceContainer> = IForgeContainerType.create { windowId: Int, inv: PlayerInventory, _: PacketBuffer? -> LavaFurnaceContainer(windowId, inv) }
        .setRegistryName(LAVA_FURNACE.registryName) as ContainerType<LavaFurnaceContainer>

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
val LAVA_FURNACE_TILEENTITY: TileEntityType<*> = TileEntityType.Builder
        .create(Supplier(::LavaFurnaceTileEntity), LAVA_FURNACE)
        .build(null)
        .setRegistryName(LAVA_FURNACE.registryName)
