package prc94.lavafurnace

import javax.annotation.Nonnull
import net.minecraft.block.{Block, FurnaceBlock}
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.stats.Stats
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockReader, World}


class LavaFurnaceBlock(builder: Block.Properties) extends FurnaceBlock(builder) {

  override def createNewTileEntity(worldIn: IBlockReader) = new LavaFurnaceTileEntity

  override protected def interactWith(worldIn: World, @Nonnull pos: BlockPos, @Nonnull player: PlayerEntity): Unit = {
    val tileentity = worldIn.getTileEntity(pos)
    if (tileentity.isInstanceOf[LavaFurnaceTileEntity]) {
      player.openContainer(tileentity.asInstanceOf[INamedContainerProvider])
      player.addStat(Stats.INTERACT_WITH_FURNACE)
    }
  }

}
