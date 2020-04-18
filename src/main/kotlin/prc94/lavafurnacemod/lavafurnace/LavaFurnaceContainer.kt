package prc94.lavafurnacemod.lavafurnace

import net.minecraft.client.util.RecipeBookCategories
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.container.AbstractFurnaceContainer
import net.minecraft.inventory.container.FurnaceFuelSlot
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.util.IIntArray
import net.minecraft.util.IntArray
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import prc94.lavafurnacemod.LAVA_FURNACE_CONTAINER

class LavaFurnaceContainer(id: Int, playerInventoryIn: PlayerInventory,
                           furnaceInventory: IInventory = Inventory(3),
                           furnaceData: IIntArray = IntArray(4))
    : AbstractFurnaceContainer(LAVA_FURNACE_CONTAINER, IRecipeType.SMELTING,
        id, playerInventoryIn, furnaceInventory, furnaceData) {

    override fun addSlot(slotIn: Slot): Slot =
            if (slotIn is FurnaceFuelSlot)
                super.addSlot(FakeFurnaceFuelSlot(slotIn))
            else
                super.addSlot(slotIn)

    override fun getRecipeBookCategories(): List<RecipeBookCategories> =
            listOf(RecipeBookCategories.FURNACE_SEARCH,
                    RecipeBookCategories.FURNACE_FOOD,
                    RecipeBookCategories.FURNACE_BLOCKS,
                    RecipeBookCategories.FURNACE_MISC)

    /**
     * Fake slot to replace default furnace fuel slot. Always disabled and can't accept any items.
     */
    private class FakeFurnaceFuelSlot(origin: FurnaceFuelSlot) : Slot(origin.inventory, origin.slotIndex, origin.xPos, origin.yPos) {
        @OnlyIn(Dist.CLIENT)
        override fun isEnabled(): Boolean = false

        override fun isItemValid(stack: ItemStack): Boolean = false

        override fun getStack(): ItemStack = ItemStack.EMPTY
    }
}