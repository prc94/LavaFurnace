package prc94.lavafurnacemod.lavafurnace

import net.minecraft.block.FurnaceBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.stats.Stats.INTERACT_WITH_FURNACE
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraft.world.World

class LavaFurnaceBlock(builder: Properties) : FurnaceBlock(builder) {

    override fun createNewTileEntity(worldIn: IBlockReader) = LavaFurnaceTileEntity()

    override fun interactWith(worldIn: World, pos: BlockPos, player: PlayerEntity) =
            worldIn.getTileEntity(pos).let {
                if (it is LavaFurnaceTileEntity) {
                    player.openContainer(it)
                    player.addStat(INTERACT_WITH_FURNACE)
                }
            }
}