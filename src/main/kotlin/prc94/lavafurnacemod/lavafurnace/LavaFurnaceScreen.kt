package prc94.lavafurnacemod.lavafurnace

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui
import net.minecraft.client.gui.recipebook.FurnaceRecipeGui
import net.minecraft.client.gui.recipebook.IRecipeShownListener
import net.minecraft.client.gui.recipebook.RecipeBookGui
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.widget.button.Button
import net.minecraft.client.gui.widget.button.ImageButton
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.ClickType
import net.minecraft.inventory.container.Slot
import net.minecraft.item.Items
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import prc94.lavafurnacemod.MOD_ID

/**
 * Based on AbstractFurnaceScreen code
 *
 * @see net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen
 */
@OnlyIn(Dist.CLIENT)
class LavaFurnaceScreen(container: LavaFurnaceContainer, inv: PlayerInventory, titleIn: ITextComponent) : ContainerScreen<LavaFurnaceContainer>(container, inv, titleIn), IRecipeShownListener {
    private val btnTexture = ResourceLocation("textures/gui/recipe_button.png")
    private val textures = ResourceLocation(MOD_ID, "textures/gui/container/lava_furnace.png")
    private val recipeBook: AbstractRecipeBookGui = object : FurnaceRecipeGui() {
        override fun func_212958_h() = setOf(Items.LAVA_BUCKET)
    }
    private var wide = false

    override fun init() {
        super.init()
        wide = width < 379
        recipeBook.func_201520_a(width, height, minecraft!!, wide, container)
        guiLeft = recipeBook.updateScreenPosition(wide, width, xSize)
        addButton(ImageButton(
                guiLeft + 20,
                height / 2 - 49,
                20, 18, 0, 0, 19,
                btnTexture) { btn: Button ->
            recipeBook.func_201518_a(wide)
            recipeBook.toggleVisibility()
            guiLeft = recipeBook.updateScreenPosition(wide, width, xSize)
            if (btn is ImageButton)
                btn.setPosition(guiLeft + 20, height / 2 - 49)
        })
    }

    override fun tick() {
        super.tick()
        recipeBook.tick()
    }

    override fun render(p_render_1: Int, p_render_2: Int, p_render_3: Float) {
        this.renderBackground()
        if (recipeBook.isVisible && wide) {
            drawGuiContainerBackgroundLayer(p_render_3, p_render_1, p_render_2)
            recipeBook.render(p_render_1, p_render_2, p_render_3)
        } else {
            recipeBook.render(p_render_1, p_render_2, p_render_3)
            super.render(p_render_1, p_render_2, p_render_3)
            recipeBook.renderGhostRecipe(guiLeft, guiTop, true, p_render_3)
        }
        renderHoveredToolTip(p_render_1, p_render_2)
        recipeBook.renderTooltip(guiLeft, guiTop, p_render_1, p_render_2)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        title.formattedText.let { s ->
            font.drawString(s, (xSize / 2 - font.getStringWidth(s) / 2).toFloat(), 6.0F, 4210752)
            font.drawString(playerInventory.displayName.formattedText, 8.0F, (ySize - 96 + 2).toFloat(), 4210752)
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F)
        minecraft!!.textureManager.bindTexture(textures)
        val i = guiLeft
        val j = guiTop
        this.blit(i, j, 0, 0, xSize, ySize)
        if (container.isBurning()) {
            val k = container.getBurnLeftScaled()
            this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1)
        }
        this.blit(i + 79, j + 34, 176, 14, container.getCookProgressionScaled() + 1, 16)
    }

    override fun mouseClicked(pMouseClicked1: Double, pMouseClicked3: Double, pMouseClicked5: Int): Boolean =
            if (recipeBook.mouseClicked(pMouseClicked1, pMouseClicked3, pMouseClicked5)) true
            else wide && recipeBook.isVisible || super.mouseClicked(pMouseClicked1, pMouseClicked3, pMouseClicked5)

    override fun handleMouseClick(slotIn: Slot?, slotId: Int, mouseButton: Int, type: ClickType) {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        super.handleMouseClick(slotIn, slotId, mouseButton, type)
        recipeBook.slotClicked(slotIn)
    }

    override fun keyPressed(pKeyPressed1: Int, pKeyPressed2: Int, pKeyPressed3: Int): Boolean = !recipeBook.keyPressed(pKeyPressed1, pKeyPressed2, pKeyPressed3) && super.keyPressed(pKeyPressed1, pKeyPressed2, pKeyPressed3)

    override fun hasClickedOutside(p1953611: Double, p1953613: Double, p1953615: Int, p1953616: Int, p1953617: Int): Boolean {
        val flag = p1953611 < p1953615.toDouble() || p1953613 < p1953616.toDouble() || p1953611 >= (p1953615 + xSize).toDouble() || p1953613 >= (p1953616 + ySize).toDouble()
        return recipeBook.func_195604_a(p1953611, p1953613, guiLeft, guiTop, xSize, ySize, p1953617) && flag
    }

    override fun charTyped(pCharTyped1: Char, pCharTyped2: Int): Boolean = recipeBook.charTyped(pCharTyped1, pCharTyped2) || super.charTyped(pCharTyped1, pCharTyped2)

    override fun recipesUpdated() = recipeBook.recipesUpdated()

    override fun func_194310_f(): RecipeBookGui = recipeBook

    override fun removed() {
        recipeBook.removed()
        super.removed()
    }

}

