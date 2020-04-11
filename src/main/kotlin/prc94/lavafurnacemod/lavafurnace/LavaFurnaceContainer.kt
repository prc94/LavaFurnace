package prc94.lavafurnacemod.lavafurnace

import com.google.common.collect.Lists
import net.minecraft.client.util.RecipeBookCategories
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.IRecipeHelperPopulator
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.container.*
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.*
import net.minecraft.util.IIntArray
import net.minecraft.util.IntArray
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import prc94.lavafurnacemod.LAVA_FURNACE_CONTAINER

/**
 * Based on AbstractFurnaceContainer's code
 * @see AbstractFurnaceContainer
 */
class LavaFurnaceContainer(id: Int, playerInventoryIn: PlayerInventory,
                           private val furnaceInventory: IInventory = Inventory(3),
                           private val furnaceData: IIntArray = IntArray(4)) : RecipeBookContainer<IInventory>(LAVA_FURNACE_CONTAINER, id) {

    private val recipeType: IRecipeType<FurnaceRecipe> = IRecipeType.SMELTING
    private val world = playerInventoryIn.player.world

    init {
        Container.assertInventorySize(furnaceInventory, 3)
        Container.assertIntArraySize(furnaceData, 4)
        this.addSlot(Slot(furnaceInventory, 0, 56, 17))
        this.addSlot(FakeFurnaceFuelSlot(furnaceInventory, 1, 56, 53))
        this.addSlot(FurnaceResultSlot(playerInventoryIn.player, furnaceInventory, 2, 116, 35))
        for (i in 0 until 3) {
            for (j in 0 until 9) {
                this.addSlot(Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }
        for (k in 0 until 9) {
            this.addSlot(Slot(playerInventoryIn, k, 8 + k * 18, 142))
        }
        this.trackIntArray(furnaceData)
    }

    override fun func_201771_a(p_201771_1: RecipeItemHelper) {
        if (furnaceInventory is IRecipeHelperPopulator)
            furnaceInventory.fillStackedContents(p_201771_1)
    }

    override fun clear() = this.furnaceInventory.clear()

    override fun func_217056_a(p_217056_1: Boolean, p_217056_2: IRecipe<*>, p_217056_3: ServerPlayerEntity) {
        @Suppress("UNCHECKED_CAST")
        ServerRecipePlacerFurnace(this).place(p_217056_3, p_217056_2 as IRecipe<IInventory>, p_217056_1)
    }

    override fun matches(recipeIn: IRecipe<in IInventory>): Boolean = recipeIn.matches(this.furnaceInventory, this.world)

    override fun getOutputSlot() = 2

    override fun getWidth() = 1

    override fun getHeight() = 1

    @OnlyIn(Dist.CLIENT)
    override fun getSize() = 3

    override fun canInteractWith(playerIn: PlayerEntity): Boolean = this.furnaceInventory.isUsableByPlayer(playerIn)

    override fun transferStackInSlot(playerIn: PlayerEntity, index: Int): ItemStack {
        var itemstack = ItemStack.EMPTY
        this.inventorySlots[index].let { slot: Slot? ->
            if (slot != null && slot.hasStack) {
                val itemstack1 = slot.stack
                itemstack = itemstack1.copy()
                if (index == 2) {
                    if (!this.mergeItemStack(itemstack1, 3, 39, true)) return ItemStack.EMPTY
                    slot.onSlotChange(itemstack1, itemstack)
                } else if (index != 1 && index != 0) if (this.func217057a(itemstack1)) if (!this.mergeItemStack(itemstack1, 0, 1, false)) return ItemStack.EMPTY
                else if (index < 30) if (!this.mergeItemStack(itemstack1, 30, 39, false)) return ItemStack.EMPTY
                else if (index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) return ItemStack.EMPTY
                else if (!this.mergeItemStack(itemstack1, 3, 39, false)) return ItemStack.EMPTY
                if (itemstack1.isEmpty) slot.putStack(ItemStack.EMPTY)
                else slot.onSlotChanged()
                if (itemstack1.count == itemstack.count) return ItemStack.EMPTY
                slot.onTake(playerIn, itemstack1)
            }
        }
        return itemstack
    }

    private fun func217057a(p_217057_1: ItemStack): Boolean = this.world.recipeManager.getRecipe(recipeType, Inventory(p_217057_1), world).isPresent


    @OnlyIn(Dist.CLIENT)
    fun getCookProgressionScaled(): Int {
        val i = this.furnaceData.get(2)
        val j = this.furnaceData.get(3)
        return if (j != 0 && i != 0) i * 24 / j else 0
    }

    @OnlyIn(Dist.CLIENT)
    fun getBurnLeftScaled(): Int = if (this.furnaceData.get(0) > 0) 100 else 0

    override fun getRecipeBookCategories(): List<RecipeBookCategories> =
            Lists.newArrayList(RecipeBookCategories.FURNACE_SEARCH, RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC)

    @OnlyIn(Dist.CLIENT)
    fun isBurning(): Boolean = this.furnaceData.get(0) > 0

    /**
     * Fake slot to replace default furnace fuel slot. Always disabled and can't accept any items.
     */
    private inner class FakeFurnaceFuelSlot(inventory: IInventory, index: Int, xPos: Int, yPos: Int) : Slot(inventory, index, xPos, yPos) {
        @OnlyIn(Dist.CLIENT)
        override fun isEnabled(): Boolean = false

        override fun isItemValid(stack: ItemStack): Boolean = false
    }

}

