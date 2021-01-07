package bletch.tektopiabanker.core;

import bletch.tektopiabanker.blocks.BlockBankersBell;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {

	public static BlockBankersBell BankersBell = new BlockBankersBell();
	
	public static void register(IForgeRegistry<Block> registry) {
		registry.register(BankersBell);
	}

	public static void registerItemBlocks(IForgeRegistry<Item> registry) {
		registry.register(new ItemBlock(BankersBell).setRegistryName(BankersBell.getRegistryName()));
	}
	
}
