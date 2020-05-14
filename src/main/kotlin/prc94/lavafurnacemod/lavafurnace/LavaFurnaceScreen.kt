package prc94.lavafurnacemod.lavafurnace

import net.minecraft.client.gui.recipebook.FurnaceRecipeGui
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.Items
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import prc94.lavafurnacemod.MOD_ID

@OnlyIn(Dist.CLIENT)
class LavaFurnaceScreen(container: LavaFurnaceContainer, inv: PlayerInventory, titleIn: ITextComponent)
    : AbstractFurnaceScreen<LavaFurnaceContainer>(container, recipeBook, inv, titleIn, textures) {

    companion object {
        private val textures = ResourceLocation(MOD_ID, "textures/gui/container/lava_furnace.png")
        private val recipeBook = object : FurnaceRecipeGui() {
            val items = setOf(Items.LAVA_BUCKET)
            override fun func_212958_h() = items
        }
    }
}