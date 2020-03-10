package prc94.lavafurnace;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import prc94.lavafurnace.LavaFurnaceBlock.LavaFurnaceTileEntity;

/**
 * Class storing all objects that need to be registered via {@link IForgeRegistry#register(IForgeRegistryEntry)}
 */
public class RegistryObjects {
    public static final Block LAVA_FURNACE = new LavaFurnaceBlock(Block.Properties.create(Material.ROCK, MaterialColor.BLACK).hardnessAndResistance(50.0F, 1200.0F)).setRegistryName("lava_furnace");

    public static final ContainerType<LavaFurnaceContainer> LAVA_FURNACE_CONTAINER = (IForgeContainerType.create((windowId, inv, data) -> new LavaFurnaceContainer(windowId, inv)));

    @SuppressWarnings("ConstantConditions")
    public static final TileEntityType<LavaFurnaceTileEntity> LAVA_FURNACE_TILEENTITY = TileEntityType.Builder.create(LavaFurnaceTileEntity::new, LAVA_FURNACE).build(null);
}
