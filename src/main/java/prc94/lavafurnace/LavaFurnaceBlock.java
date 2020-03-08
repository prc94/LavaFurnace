package prc94.lavafurnace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.recipebook.FurnaceRecipeGui;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

//TODO Make this shit work as intended
public class LavaFurnaceBlock extends FurnaceBlock {

    protected LavaFurnaceBlock(Properties builder) {
        super(builder);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new LavaFurnaceTileEntity();
    }

    @Override
    protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof LavaFurnaceTileEntity) {
            player.openContainer((INamedContainerProvider) tileentity);
            player.addStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @SuppressWarnings("unchecked")
    public static class LavaFurnaceContainer extends RecipeBookContainer<IInventory> {
        protected final World world;
        private final IInventory furnaceInventory;
        private final IIntArray furnaceData;
        private final IRecipeType<? extends AbstractCookingRecipe> recipeType;

        protected LavaFurnaceContainer(int id, PlayerInventory playerInventoryIn) {
            this(id, playerInventoryIn, new Inventory(3), new IntArray(4));
        }

        protected LavaFurnaceContainer(int id, PlayerInventory playerInventoryIn, IInventory furnaceInventoryIn, IIntArray furnaceData) {
            super(RegistryObjects.LAVA_FURNACE_CONTAINER, id);
            this.recipeType = IRecipeType.SMELTING;
            assertInventorySize(furnaceInventoryIn, 3);
            assertIntArraySize(furnaceData, 4);
            this.furnaceInventory = furnaceInventoryIn;
            this.furnaceData = furnaceData;
            this.world = playerInventoryIn.player.world;
            this.addSlot(new Slot(furnaceInventoryIn, 0, 56, 17));
            this.addSlot(new FakeFurnaceFuelSlot(furnaceInventoryIn, 1, 56, 53));
            this.addSlot(new FurnaceResultSlot(playerInventoryIn.player, furnaceInventoryIn, 2, 116, 35));

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
            }

            this.trackIntArray(furnaceData);
        }

        @Override
        public void func_201771_a(RecipeItemHelper p_201771_1_) {
            if (this.furnaceInventory instanceof IRecipeHelperPopulator) {
                ((IRecipeHelperPopulator) this.furnaceInventory).fillStackedContents(p_201771_1_);
            }

        }

        @Override
        public void clear() {
            this.furnaceInventory.clear();
        }

        @Override
        public void func_217056_a(boolean p_217056_1_, IRecipe<?> p_217056_2_, ServerPlayerEntity p_217056_3_) {
            (new ServerRecipePlacerFurnace<>(this)).place(p_217056_3_, (IRecipe<IInventory>) p_217056_2_, p_217056_1_);
        }

        @Override
        public boolean matches(IRecipe<? super IInventory> recipeIn) {
            return recipeIn.matches(this.furnaceInventory, this.world);
        }

        @Override
        public int getOutputSlot() {
            return 2;
        }

        @Override
        public int getWidth() {
            return 1;
        }

        @Override
        public int getHeight() {
            return 1;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public int getSize() {
            return 3;
        }

        /*
         * Determines whether supplied player can use this container
         */
        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return this.furnaceInventory.isUsableByPlayer(playerIn);
        }

        /**
         * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
         * inventory and the other inventory(s).
         */
        @Override
        public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
            ItemStack itemstack = ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
                ItemStack itemstack1 = slot.getStack();
                itemstack = itemstack1.copy();
                if (index == 2) {
                    if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                        return ItemStack.EMPTY;
                    }

                    slot.onSlotChange(itemstack1, itemstack);
                } else if (index != 1 && index != 0) {
                    if (this.func_217057_a(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (this.isFuel(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 30) {
                        if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }

                if (itemstack1.isEmpty()) {
                    slot.putStack(ItemStack.EMPTY);
                } else {
                    slot.onSlotChanged();
                }

                if (itemstack1.getCount() == itemstack.getCount()) {
                    return ItemStack.EMPTY;
                }

                slot.onTake(playerIn, itemstack1);
            }

            return itemstack;
        }

        protected boolean func_217057_a(ItemStack p_217057_1_) {
            return this.world.getRecipeManager().getRecipe((IRecipeType) this.recipeType, new Inventory(p_217057_1_), this.world).isPresent();
        }

        protected boolean isFuel(ItemStack p_217058_1_) {
            return LavaFurnaceTileEntity.isFuel(p_217058_1_);
        }

        @OnlyIn(Dist.CLIENT)
        public int getCookProgressionScaled() {
            int i = this.furnaceData.get(2);
            int j = this.furnaceData.get(3);
            return j != 0 && i != 0 ? i * 24 / j : 0;
        }

        @OnlyIn(Dist.CLIENT)
        public int getBurnLeftScaled() {
            int i = this.furnaceData.get(0);
            if (i > 0)
                return 100;
            else
                return 0;

            //return this.field_217064_e.get(0) * 13 / i;
        }

        @OnlyIn(Dist.CLIENT)
        public boolean func_217061_l() {
            return this.furnaceData.get(0) > 0;
        }
    }

    //Commented code in this class mostly related to Recipe Book GUI
    @OnlyIn(Dist.CLIENT)
    public static class LavaFurnaceScreen extends ContainerScreen<LavaFurnaceContainer> implements IRecipeShownListener {
        private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation(LavaFurnaceMod.MOD_ID, "textures/gui/container/lava_furnace.png");
        private final ResourceLocation field_214091_n;
        private static final ResourceLocation field_214089_l = new ResourceLocation("textures/gui/recipe_button.png");
        //FIXME Try fixing recipe book
        public final AbstractRecipeBookGui field_214088_k;
        private boolean field_214090_m;

        public LavaFurnaceScreen(LavaFurnaceContainer p_i51104_1_, PlayerInventory p_i51104_3_, ITextComponent p_i51104_4_) {
            super(p_i51104_1_, p_i51104_3_, p_i51104_4_);
            this.field_214088_k = new FurnaceRecipeGui();
            this.field_214091_n = FURNACE_GUI_TEXTURES;
        }

        @Override
        public void init() {
            super.init();
            this.field_214090_m = this.width < 379;
            assert this.minecraft != null;
            this.field_214088_k.func_201520_a(this.width, this.height, this.minecraft, this.field_214090_m, this.container);
            this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize);
            this.addButton((new ImageButton(this.guiLeft + 20, this.height / 2 - 49, 20, 18, 0, 0, 19, field_214089_l, (p_214087_1_) -> {
                this.field_214088_k.func_201518_a(this.field_214090_m);
                //this.field_214088_k.toggleVisibility();
                this.guiLeft = this.field_214088_k.updateScreenPosition(this.field_214090_m, this.width, this.xSize);
                ((ImageButton) p_214087_1_).setPosition(this.guiLeft + 20, this.height / 2 - 49);
            })));
        }

        @Override
        @SuppressWarnings("EmptyMethod")
        public void tick() {
            super.tick();
            this.field_214088_k.tick();
        }

        @Override
        public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
            this.renderBackground();
            if (this.field_214088_k.isVisible() && this.field_214090_m) {
                this.drawGuiContainerBackgroundLayer(p_render_3_, p_render_1_, p_render_2_);
                this.field_214088_k.render(p_render_1_, p_render_2_, p_render_3_);
            } else {
                this.field_214088_k.render(p_render_1_, p_render_2_, p_render_3_);
                super.render(p_render_1_, p_render_2_, p_render_3_);
                this.field_214088_k.renderGhostRecipe(this.guiLeft, this.guiTop, true, p_render_3_);
            }

            this.renderHoveredToolTip(p_render_1_, p_render_2_);
            this.field_214088_k.renderTooltip(this.guiLeft, this.guiTop, p_render_1_, p_render_2_);
        }

        /**
         * Draw the foreground layer for the GuiContainer (everything in front of the items)
         */
        @Override
        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
            String s = this.title.getFormattedText();
            this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 6.0F, 4210752);
            this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
        }

        /**
         * Draws the background layer of this container (behind the items).
         */
        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            assert this.minecraft != null;
            this.minecraft.getTextureManager().bindTexture(this.field_214091_n);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(i, j, 0, 0, this.xSize, this.ySize);
            if (this.container.func_217061_l()) {
                int k = this.container.getBurnLeftScaled();
                this.blit(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
            }

            int l = this.container.getCookProgressionScaled();
            this.blit(i + 79, j + 34, 176, 14, l + 1, 16);
        }

        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (this.field_214088_k.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                return true;
            } else {
                return this.field_214090_m && this.field_214088_k.isVisible() || super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
            }
        }

        /**
         * Called when the mouse is clicked over a slot or outside the gui.
         */
        @Override
        protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
            super.handleMouseClick(slotIn, slotId, mouseButton, type);
            this.field_214088_k.slotClicked(slotIn);
        }

        public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
            return !this.field_214088_k.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        }

        protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
            boolean flag = p_195361_1_ < (double) p_195361_5_ || p_195361_3_ < (double) p_195361_6_ || p_195361_1_ >= (double) (p_195361_5_ + this.xSize) || p_195361_3_ >= (double) (p_195361_6_ + this.ySize);
            return this.field_214088_k.func_195604_a(p_195361_1_, p_195361_3_, this.guiLeft, this.guiTop, this.xSize, this.ySize, p_195361_7_) && flag;
        }

        public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
            return this.field_214088_k.charTyped(p_charTyped_1_, p_charTyped_2_) || super.charTyped(p_charTyped_1_, p_charTyped_2_);
        }

        @Override
        public void recipesUpdated() {
            this.field_214088_k.recipesUpdated();
        }

        @Override
        public RecipeBookGui func_194310_f() {
            return this.field_214088_k;
        }

        @Override
        @SuppressWarnings("EmptyMethod")
        public void removed() {
            this.field_214088_k.removed();
            super.removed();
        }
    }

    @SuppressWarnings({"unchecked", "NullableProblems", "FieldCanBeLocal"})
    public static class LavaFurnaceTileEntity extends AbstractFurnaceTileEntity {
        private int burnTime = this.furnaceData.get(0);
        private int recipesUsed = this.furnaceData.get(1);
        private int cookTime = this.furnaceData.get(2);
        private int cookTimeTotal = this.furnaceData.get(3);

        protected LavaFurnaceTileEntity() {
            super(RegistryObjects.LAVA_FURNACE_TILEENTITY, IRecipeType.SMELTING);
        }

        @Override
        protected ITextComponent getDefaultName() {
            return new TranslationTextComponent("container.furnace");
        }

        @Override
        protected Container createMenu(int id, PlayerInventory player) {
            return new LavaFurnaceContainer(id, player, this, this.furnaceData);
        }

        private boolean isBurning() {
            return this.burnTime > 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void tick() {
            this.burnTime = this.furnaceData.get(0);
            this.recipesUsed = this.furnaceData.get(1);
            this.cookTime = this.furnaceData.get(2);
            this.cookTimeTotal = this.furnaceData.get(3);

            boolean flag = this.isBurning();
            boolean flag1 = false;

            assert this.world != null;
            if (this.world.getBlockState(pos.down()).getBlock().equals(Blocks.LAVA))
                this.burnTime = 100;
            else
                this.burnTime = 0;

            if (!this.world.isRemote) {
                //ItemStack itemstack = this.items.get(1);
                if (this.isBurning() /*|| !itemstack.isEmpty()*/ && !this.items.get(0).isEmpty()) {
                    IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>) this.recipeType, this, this.world).orElse(null);
                    if (!this.isBurning() && this.canSmelt(irecipe)) {
                        //this.burnTime = this.getBurnTime(itemstack);
                        this.recipesUsed = this.burnTime;
                        if (this.isBurning()) {
                            flag1 = true;
                            /*if (itemstack.hasContainerItem())
                                this.items.set(1, itemstack.getContainerItem());
                            else if (!itemstack.isEmpty()) {
                                Item item = itemstack.getItem();
                                itemstack.shrink(1);
                                if (itemstack.isEmpty()) {
                                    this.items.set(1, itemstack.getContainerItem());
                                }
                            }*/
                        }
                    }

                    if (this.isBurning() && this.canSmelt(irecipe)) {
                        ++this.cookTime;
                        if (this.cookTime == this.cookTimeTotal) {
                            this.cookTime = 0;
                            this.cookTimeTotal = this.func_214005_h();
                            this.smelt(irecipe);
                            flag1 = true;
                        }
                    } else {
                        this.cookTime = 0;
                    }
                } else if (!this.isBurning() && this.cookTime > 0) {
                    this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
                }

                if (flag != this.isBurning()) {
                    flag1 = true;
                    this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
                }
            }

            if (flag1) {
                this.markDirty();
            }

            this.furnaceData.set(0, this.burnTime);
            this.furnaceData.set(1, this.recipesUsed);
            this.furnaceData.set(2, this.cookTime);
            this.furnaceData.set(3, this.cookTimeTotal);

        }

        private void smelt(@Nullable IRecipe<?> recipe) {
            if (recipe != null && this.canSmelt(recipe)) {
                ItemStack itemstack = this.items.get(0);
                ItemStack itemstack1 = recipe.getRecipeOutput();
                ItemStack itemstack2 = this.items.get(2);
                if (itemstack2.isEmpty()) {
                    this.items.set(2, itemstack1.copy());
                } else if (itemstack2.getItem() == itemstack1.getItem()) {
                    itemstack2.grow(itemstack1.getCount());
                }

                assert this.world != null;
                if (!this.world.isRemote) {
                    this.setRecipeUsed(recipe);
                }

                if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
                    this.items.set(1, new ItemStack(Items.WATER_BUCKET));
                }

                itemstack.shrink(1);
            }
        }
    }

    static class FakeFurnaceFuelSlot extends Slot {

        public FakeFurnaceFuelSlot(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
            super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isEnabled() {
            return false;
        }
    }


}
