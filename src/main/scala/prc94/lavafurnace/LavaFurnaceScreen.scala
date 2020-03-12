package prc94.lavafurnace

import java.util

import com.google.common.collect.Sets
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.recipebook.{AbstractRecipeBookGui, FurnaceRecipeGui, IRecipeShownListener, RecipeBookGui}
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.gui.widget.button.{Button, ImageButton}
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.{ClickType, Slot}
import net.minecraft.item.{Item, Items}
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.api.distmarker.{Dist, OnlyIn}

@OnlyIn(Dist.CLIENT)
class LavaFurnaceScreen(container: LavaFurnaceContainer, inv: PlayerInventory, titleIn: ITextComponent)
  extends ContainerScreen[LavaFurnaceContainer](container, inv, titleIn)
    with IRecipeShownListener {
  val FURNACE_GUI_TEXTURES = new ResourceLocation(LavaFurnaceMod.MOD_ID, "textures/gui/container/lava_furnace.png")
  val field_214089_l = new ResourceLocation("textures/gui/recipe_button.png")
  private val field_214091_n = FURNACE_GUI_TEXTURES
  var field_214088_k: AbstractRecipeBookGui = new FurnaceRecipeGui() {
    private val items = Sets.newHashSet[Item]()
    items.add(Items.AIR)

    override def func_212958_h(): util.Set[Item] = items
  }
  private var field_214090_m = false

  override def init(): Unit = {
    super.init()
    this.field_214090_m = this.width < 379
    assert(this.minecraft != null)
    this.field_214088_k.func_201520_a(this.width, this.height, this.minecraft, this.field_214090_m, this.container)
    this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize)
    this.addButton(new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_214089_l, (btn: Button) => {
      this.field_214088_k.func_201518_a(this.field_214090_m)
      this.field_214088_k.toggleVisibility()
      this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize)
      btn.asInstanceOf[ImageButton].setPosition(this.guiLeft + 20, this.height / 2 - 49)
    }))
  }

  override def tick(): Unit = {
    super.tick()
    this.field_214088_k.tick()
  }

  override def render(p_render_1: Int, p_render_2: Int, p_render_3: Float): Unit = {
    this.renderBackground()
    if (this.field_214088_k.isVisible && this.field_214090_m) {
      this.drawGuiContainerBackgroundLayer(p_render_3, p_render_1, p_render_2)
      this.field_214088_k.render(p_render_1, p_render_2, p_render_3)
    }
    else {
      this.field_214088_k.render(p_render_1, p_render_2, p_render_3)
      super.render(p_render_1, p_render_2, p_render_3)
      this.field_214088_k.renderGhostRecipe(this.guiLeft, this.guiTop, true, p_render_3)
    }
    this.renderHoveredToolTip(p_render_1, p_render_2)
    this.field_214088_k.renderTooltip(this.guiLeft, this.guiTop, p_render_1, p_render_2)
  }

  override protected def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit = {
    val s = this.title.getFormattedText
    this.font.drawString(s, (this.xSize / 2 - this.font.getStringWidth(s) / 2).toFloat, 6.0F, 4210752)
    this.font.drawString(this.playerInventory.getDisplayName.getFormattedText, 8.0F, (this.ySize - 96 + 2).toFloat, 4210752)
  }

  override protected def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int): Unit = {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F)
    assert(this.minecraft != null)
    this.minecraft.getTextureManager.bindTexture(this.field_214091_n)
    val i = this.guiLeft
    val j = this.guiTop
    this.blit(i, j, 0, 0, this.xSize, this.ySize)
    if (this.container.func_217061_l) {
      val k = this.container.getBurnLeftScaled
      this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1)
    }
    val l = this.container.getCookProgressionScaled
    this.blit(i + 79, j + 34, 176, 14, l + 1, 16)
  }

  override def mouseClicked(pMouseClicked1: Double, pMouseClicked3: Double, pMouseClicked5: Int): Boolean = if (this.field_214088_k.mouseClicked(pMouseClicked1, pMouseClicked3, pMouseClicked5)) true
  else this.field_214090_m && this.field_214088_k.isVisible || super.mouseClicked(pMouseClicked1, pMouseClicked3, pMouseClicked5)

  override protected def handleMouseClick(slotIn: Slot, slotId: Int, mouseButton: Int, `type`: ClickType): Unit = {
    super.handleMouseClick(slotIn, slotId, mouseButton, `type`)
    this.field_214088_k.slotClicked(slotIn)
  }

  override def keyPressed(pKeyPressed1: Int, pKeyPressed2: Int, pKeyPressed3: Int): Boolean = !this.field_214088_k.keyPressed(pKeyPressed1, pKeyPressed2, pKeyPressed3) && super.keyPressed(pKeyPressed1, pKeyPressed2, pKeyPressed3)

  override protected def hasClickedOutside(p1953611: Double, p1953613: Double, p1953615: Int, p1953616: Int, p1953617: Int): Boolean = {
    val flag = p1953611 < p1953615.toDouble || p1953613 < p1953616.toDouble || p1953611 >= (p1953615 + this.xSize).toDouble || p1953613 >= (p1953616 + this.ySize).toDouble
    this.field_214088_k.func_195604_a(p1953611, p1953613, this.guiLeft, this.guiTop, this.xSize, this.ySize, p1953617) && flag
  }

  override def charTyped(pCharTyped1: Char, pCharTyped2: Int): Boolean = this.field_214088_k.charTyped(pCharTyped1, pCharTyped2) || super.charTyped(pCharTyped1, pCharTyped2)

  override def recipesUpdated(): Unit = {
    this.field_214088_k.recipesUpdated()
  }

  override def func_194310_f: RecipeBookGui = this.field_214088_k

  override def removed(): Unit = {
    this.field_214088_k.removed()
    super.removed()
  }

}

