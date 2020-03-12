package prc94.lavafurnace

import javax.annotation.{Nonnull, Nullable}
import net.minecraft.block.{AbstractFurnaceBlock, Blocks}
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.item.crafting.{FurnaceRecipe, IRecipe, IRecipeType}
import net.minecraft.item.{ItemStack, Items}
import net.minecraft.state.BooleanProperty
import net.minecraft.tileentity.AbstractFurnaceTileEntity
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TranslationTextComponent

class LavaFurnaceTileEntity extends AbstractFurnaceTileEntity(RegistryObjects.LAVA_FURNACE_TILEENTITY, IRecipeType.SMELTING) {
  private var burnTime = this.furnaceData.get(0)
  private var recipesUsed = this.furnaceData.get(1)
  private var cookTime = this.furnaceData.get(2)
  private var cookTimeTotal = this.furnaceData.get(3)

  override protected def getDefaultName = new TranslationTextComponent("container.furnace")

  override protected def createMenu(id: Int, @Nonnull player: PlayerInventory) = new LavaFurnaceContainer(id, player, this, this.furnaceData)

  private def isBurning = this.burnTime > 0

  override def tick(): Unit = {
    this.burnTime = this.furnaceData.get(0)
    this.recipesUsed = this.furnaceData.get(1)
    this.cookTime = this.furnaceData.get(2)
    this.cookTimeTotal = this.furnaceData.get(3)
    val flag = this.isBurning
    var flag1 = false
    assert(this.world != null)
    if (this.world.getBlockState(pos.down).getBlock == Blocks.LAVA) this.burnTime = 100
    else this.burnTime = 0
    if (!this.world.isRemote) {
      if (this.isBurning && !this.items.get(0).isEmpty) {
        val irecipe = this.world.getRecipeManager.getRecipe(this.recipeType.asInstanceOf[IRecipeType[FurnaceRecipe]], this: IInventory, this.world).orElse(null)
        if (!this.isBurning && this.canSmelt(irecipe)) {
          this.recipesUsed = this.burnTime
          if (this.isBurning) flag1 = true
        }
        if (this.isBurning && this.canSmelt(irecipe)) {
          this.cookTime += 1
          if (this.cookTime == this.cookTimeTotal) {
            this.cookTime = 0
            this.cookTimeTotal = this.func_214005_h
            this.smelt(irecipe)
            flag1 = true
          }
        }
        else this.cookTime = 0
      }
      else if (!this.isBurning && this.cookTime > 0) this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal)
      if (flag != this.isBurning) {
        flag1 = true
        this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).`with`(AbstractFurnaceBlock.LIT: BooleanProperty, this.isBurning: java.lang.Boolean), 3)
      }
    }
    if (flag1) this.markDirty()
    this.furnaceData.set(0, this.burnTime)
    this.furnaceData.set(1, this.recipesUsed)
    this.furnaceData.set(2, this.cookTime)
    this.furnaceData.set(3, this.cookTimeTotal)
  }

  private def smelt(@Nullable recipe: IRecipe[_]): Unit = {
    if (recipe != null && this.canSmelt(recipe)) {
      val itemstack = this.items.get(0)
      val itemstack1 = recipe.getRecipeOutput
      val itemstack2 = this.items.get(2)
      if (itemstack2.isEmpty) this.items.set(2, itemstack1.copy)
      else if (itemstack2.getItem eq itemstack1.getItem) itemstack2.grow(itemstack1.getCount)
      assert(this.world != null)
      if (!this.world.isRemote) this.setRecipeUsed(recipe)
      if ((itemstack.getItem eq Blocks.WET_SPONGE.asItem) && !this.items.get(1).isEmpty && (this.items.get(1).getItem eq Items.BUCKET)) this.items.set(1, new ItemStack(Items.WATER_BUCKET))
      itemstack.shrink(1)
    }
  }

}

