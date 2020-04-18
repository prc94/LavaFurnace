package prc94.lavafurnacemod.lavafurnace

import net.minecraft.block.AbstractFurnaceBlock
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.crafting.FurnaceRecipe
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.tileentity.AbstractFurnaceTileEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TranslationTextComponent
import prc94.lavafurnacemod.LAVA_FURNACE_TILEENTITY

class LavaFurnaceTileEntity : AbstractFurnaceTileEntity(LAVA_FURNACE_TILEENTITY, IRecipeType.SMELTING) {
    private var burnTime
        get() = furnaceData.get(0)
        set(value) = furnaceData.set(0, value)
    private var recipesUsed
        get() = furnaceData.get(1)
        set(value) = furnaceData.set(1, value)
    private var cookTime
        get() = furnaceData.get(2)
        set(value) = furnaceData.set(2, value)
    private var cookTimeTotal
        get() = furnaceData.get(3)
        set(value) = furnaceData.set(3, value)

    override fun getDefaultName() = TranslationTextComponent("container.furnace")

    override fun createMenu(id: Int, player: PlayerInventory) = LavaFurnaceContainer(id, player, this, furnaceData)

    private val isBurning
        get() = burnTime > 0

    override fun tick() {
        val flag = isBurning
        var flag1 = false
        world?.let { world ->
            if (!world.isRemote) {
                if (world.getBlockState(pos.down()).block == Blocks.LAVA) this.burnTime = 200
                else this.burnTime = 0
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
                                } else this.cookTime = 0
                            }
                } else if (!isBurning && this.cookTime > 0) this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal)
                if (flag != isBurning) {
                    flag1 = true
                    world.setBlockState(pos, world.getBlockState(pos).with(AbstractFurnaceBlock.LIT, isBurning), 3)
                }
            }
        }
        if (flag1) markDirty()
    }

    private fun smelt(recipe: IRecipe<*>?) {
        if (recipe != null && canSmelt(recipe)) {
            val itemstack = items[0]
            val itemstack1 = recipe.recipeOutput
            val itemstack2 = items[2]
            if (itemstack2.isEmpty)
                items[2] = itemstack1.copy()
            else if (itemstack2.item == itemstack1.item)
                itemstack2.grow(itemstack1.count)
            if (!world!!.isRemote())
                this.recipeUsed = recipe
            if ((itemstack.item == Blocks.WET_SPONGE.asItem()) && !items[1].isEmpty && (items[1].item == Items.BUCKET))
                items[1] = ItemStack(Items.WATER_BUCKET)
            itemstack.shrink(1)
        }
    }
}