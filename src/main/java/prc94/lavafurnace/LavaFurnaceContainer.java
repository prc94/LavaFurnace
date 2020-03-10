package prc94.lavafurnace;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.FurnaceResultSlot;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@SuppressWarnings("unchecked")
public class LavaFurnaceContainer extends RecipeBookContainer<IInventory> {
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
    public void func_201771_a(@Nonnull RecipeItemHelper p_201771_1_) {
        if (this.furnaceInventory instanceof IRecipeHelperPopulator) {
            ((IRecipeHelperPopulator) this.furnaceInventory).fillStackedContents(p_201771_1_);
        }

    }

    @Override
    public void clear() {
        this.furnaceInventory.clear();
    }

    @Override
    public void func_217056_a(boolean p_217056_1_, @Nonnull IRecipe<?> p_217056_2_, @Nonnull ServerPlayerEntity p_217056_3_) {
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
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return this.furnaceInventory.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Nonnull
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
        return this.world.getRecipeManager().getRecipe((IRecipeType<FurnaceRecipe>) this.recipeType, new Inventory(p_217057_1_), this.world).isPresent();
    }

    protected boolean isFuel(ItemStack p_217058_1_) {
        return LavaFurnaceBlock.LavaFurnaceTileEntity.isFuel(p_217058_1_);
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

    private static class FakeFurnaceFuelSlot extends Slot {

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
