package prc94.lavafurnacemod.lavafurnace

import net.minecraft.block.AbstractFurnaceBlock
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.crafting.FurnaceRecipe
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.tileentity.AbstractFurnaceTileEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TranslationTextComponent
import prc94.lavafurnacemod.LAVA_FURNACE_TILEENTITY
import prc94.lavafurnacemod.delegateIntArrayEntry

class LavaFurnaceTileEntity : AbstractFurnaceTileEntity(LAVA_FURNACE_TILEENTITY, IRecipeType.SMELTING) {
    private var burnTime by delegateIntArrayEntry(furnaceData, 0)
    private var recipesUsed by delegateIntArrayEntry(furnaceData, 1)
    private var cookTimeCurrent by delegateIntArrayEntry(furnaceData, 2)
    private var cookTimeTotal by delegateIntArrayEntry(furnaceData, 3)

    override fun getDefaultName() = TranslationTextComponent("container.furnace")

    override fun createMenu(id: Int, player: PlayerInventory) = LavaFurnaceContainer(id, player, this, furnaceData)

    private val isBurning: Boolean
        get() = burnTime > 0

    override fun tick() {
        val flag = isBurning
        var flag1 = false
        world?.let { world ->
            if (!world.isRemote) {
                //Lava detection logic here
                if (world.getBlockState(pos.down()).block == Blocks.LAVA)
                    this.burnTime = 200
                else
                    this.burnTime = 0
                if (isBurning && !items[0].isEmpty) {
                    @Suppress("UNCHECKED_CAST")
                    world.recipeManager.getRecipe(recipeType as IRecipeType<FurnaceRecipe>, this, world)
                            .orElse(null).let { irecipe ->
                                if (!isBurning && canSmelt(irecipe)) {
                                    this.recipesUsed = this.burnTime
                                    if (isBurning) flag1 = true
                                }
                                if (isBurning && canSmelt(irecipe)) {
                                    this.cookTime += 1
                                    if (this.cookTime == this.cookTimeTotal) {
                                        this.cookTime = 0
                                        this.cookTimeTotal = func_214005_h()
                                        smelt(irecipe)
                                        flag1 = true
                                    }
                                } else cookTimeCurrent = 0
                            }
                } else if (!isBurning && cookTimeCurrent > 0)
                    cookTimeCurrent = MathHelper.clamp(cookTimeCurrent - 1, 0, cookTimeTotal)
                if (flag != isBurning) {
                    flag1 = true
                    world.setBlockState(pos, world.getBlockState(pos).with(AbstractFurnaceBlock.LIT, isBurning), 3)
                }
            }
        }
        if (flag1) markDirty()
    }
}