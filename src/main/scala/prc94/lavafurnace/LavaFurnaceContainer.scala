package prc94.lavafurnace

import java.util

import com.google.common.collect.Lists
import javax.annotation.Nonnull
import net.minecraft.client.util.RecipeBookCategories
import net.minecraft.entity.player.{PlayerEntity, PlayerInventory, ServerPlayerEntity}
import net.minecraft.inventory.container.{Container, FurnaceResultSlot, RecipeBookContainer, Slot}
import net.minecraft.inventory.{IInventory, IRecipeHelperPopulator, Inventory}
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting._
import net.minecraft.util.{IIntArray, IntArray}
import net.minecraftforge.api.distmarker.{Dist, OnlyIn}

class LavaFurnaceContainer(val id: Int, val playerInventoryIn: PlayerInventory,
                           val furnaceInventory: IInventory = new Inventory(3),
                           val furnaceData: IIntArray = new IntArray(4))
  extends RecipeBookContainer[IInventory](RegistryObjects.LAVA_FURNACE_CONTAINER, id) {

  private val recipeType: IRecipeType[FurnaceRecipe] = IRecipeType.SMELTING
  private val world = playerInventoryIn.player.world
  Container.assertInventorySize(furnaceInventory, 3)
  Container.assertIntArraySize(furnaceData, 4)
  this.addSlot(new Slot(furnaceInventory, 0, 56, 17))
  this.addSlot(new FakeFurnaceFuelSlot(furnaceInventory, 1, 56, 53))
  this.addSlot(new FurnaceResultSlot(playerInventoryIn.player, furnaceInventory, 2, 116, 35))
  for (i <- 0 until 3) {
    for (j <- 0 until 9) {
      this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
    }
  }
  for (k <- 0 until 9) {
    this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142))
  }
  this.trackIntArray(furnaceData)

  override def func_201771_a(p_201771_1: RecipeItemHelper): Unit = {
    this.furnaceInventory match {
      case populator: IRecipeHelperPopulator => populator.fillStackedContents(p_201771_1)
      case _ =>
    }
  }

  override def clear(): Unit = {
    this.furnaceInventory.clear()
  }

  override def func_217056_a(p_217056_1: Boolean, @Nonnull p_217056_2: IRecipe[_], @Nonnull p_217056_3: ServerPlayerEntity): Unit = {
    new ServerRecipePlacerFurnace[IInventory](this).place(p_217056_3, p_217056_2.asInstanceOf[IRecipe[IInventory]], p_217056_1)
  }

  override def matches(recipeIn: IRecipe[_ >: IInventory]): Boolean = recipeIn.matches(this.furnaceInventory, this.world)

  override def getOutputSlot = 2

  override def getWidth = 1

  override def getHeight = 1

  @OnlyIn(Dist.CLIENT) override def getSize = 3

  /*
       * Determines whether supplied player can use this container
       */ override def canInteractWith(@Nonnull playerIn: PlayerEntity): Boolean = this.furnaceInventory.isUsableByPlayer(playerIn)

  /**
   * Handle when the stack in slot index is shift-clicked. Normally this moves the stack between the player
   * inventory and the other inventory(s).
   */
  @Nonnull override def transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack = {
    var itemstack = ItemStack.EMPTY
    val slot = this.inventorySlots.get(index)
    if (slot != null && slot.getHasStack) {
      val itemstack1 = slot.getStack
      itemstack = itemstack1.copy
      if (index == 2) {
        if (!this.mergeItemStack(itemstack1, 3, 39, true)) return ItemStack.EMPTY
        slot.onSlotChange(itemstack1, itemstack)
      }
      else if (index != 1 && index != 0) if (this.func_217057_a(itemstack1)) if (!this.mergeItemStack(itemstack1, 0, 1, false)) return ItemStack.EMPTY
      //else if (this.isFuel(itemstack1)) if (!this.mergeItemStack(itemstack1, 1, 2, false)) return ItemStack.EMPTY
      else if (index < 30) if (!this.mergeItemStack(itemstack1, 30, 39, false)) return ItemStack.EMPTY
      else if (index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) return ItemStack.EMPTY
      else if (!this.mergeItemStack(itemstack1, 3, 39, false)) return ItemStack.EMPTY
      if (itemstack1.isEmpty) slot.putStack(ItemStack.EMPTY)
      else slot.onSlotChanged()
      if (itemstack1.getCount == itemstack.getCount) return ItemStack.EMPTY
      slot.onTake(playerIn, itemstack1)
    }
    itemstack
  }

  protected def func_217057_a(p_217057_1: ItemStack): Boolean = {
    this.world.getRecipeManager.getRecipe(recipeType.asInstanceOf[IRecipeType[FurnaceRecipe]], new Inventory(p_217057_1): IInventory, world).isPresent
  }

  protected def isFuel: Boolean = false

  @OnlyIn(Dist.CLIENT) def getCookProgressionScaled: Int = {
    val i = this.furnaceData.get(2)
    val j = this.furnaceData.get(3)
    if (j != 0 && i != 0) i * 24 / j
    else 0
  }

  @OnlyIn(Dist.CLIENT) def getBurnLeftScaled: Int = {
    val i = this.furnaceData.get(0)
    if (i > 0) 100
    else 0
    //return this.field_217064_e.get(0) * 13 / i;
  }

  override def getRecipeBookCategories: util.List[RecipeBookCategories] = Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC)

  @OnlyIn(Dist.CLIENT) def func_217061_l: Boolean = this.furnaceData.get(0) > 0

  private class FakeFurnaceFuelSlot(inventory: IInventory, index: Int, xPos: Int, yPos: Int) extends Slot(inventory, index, xPos, yPos) {
    @OnlyIn(Dist.CLIENT) override def isEnabled: Boolean = false

    override def isItemValid(stack: ItemStack): Boolean = false
  }

}

