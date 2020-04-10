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
        get() = this.furnaceData.get(0)
        set(value) = this.furnaceData.set(0, value)
    private var recipesUsed
        get() = this.furnaceData.get(1)
        set(value) = this.furnaceData.set(1, value)
    private var cookTime
        get() = this.furnaceData.get(2)
        set(value) = this.furnaceData.set(2, value)
    private var cookTimeTotal
        get() = this.furnaceData.get(3)
        set(value) = this.furnaceData.set(3, value)

    override fun getDefaultName() = TranslationTextComponent("container.furnace")

    override fun createMenu(id: Int, player: PlayerInventory) = LavaFurnaceContainer(id, player, this, this.furnaceData)

    private fun isBurning() = this.burnTime > 0

    @Suppress("UNCHECKED_CAST")
    override fun tick() {
        val flag = this.isBurning()
        var flag1 = false
        if (this.world?.isRemote != true) {
            if (this.world!!.getBlockState(pos.down()).block == Blocks.LAVA) this.burnTime = 100
            else this.burnTime = 0
            if (this.isBurning() && !this.items[0].isEmpty) {
                val irecipe: IRecipe<*>? = this.world!!.recipeManager.getRecipe(this.recipeType as IRecipeType<FurnaceRecipe>, this, this.world!!).orElse(null)
                if (!this.isBurning() && this.canSmelt(irecipe)) {
                    this.recipesUsed = this.burnTime
                    if (this.isBurning()) flag1 = true
                }
                if (this.isBurning() && this.canSmelt(irecipe)) {
                    this.cookTime += 1
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0
                        this.cookTimeTotal = this.func_214005_h()
                        this.smelt(irecipe)
                        flag1 = true
                    }
                } else this.cookTime = 0
            } else if (!this.isBurning() && this.cookTime > 0) this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal)
            if (flag != this.isBurning()) {
                flag1 = true
                this.world!!.setBlockState(this.pos, this.world!!.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3)
            }
        }
        if (flag1) this.markDirty()
    }

    private fun smelt(recipe: IRecipe<*>?) {
        if (recipe != null && this.canSmelt(recipe)) {
            val itemstack = this.items[0]
            val itemstack1 = recipe.recipeOutput
            val itemstack2 = this.items[2]
            if (itemstack2.isEmpty) this.items[2] = itemstack1.copy()
            else if (itemstack2.item == itemstack1.item) itemstack2.grow(itemstack1.count)
            if (!this.world!!.isRemote()) this.recipeUsed = recipe
            if ((itemstack.item == Blocks.WET_SPONGE.asItem()) && !this.items[1].isEmpty && (this.items[1].item == Items.BUCKET)) this.items[1] = ItemStack(Items.WATER_BUCKET)
            itemstack.shrink(1)
        }
    }

}
