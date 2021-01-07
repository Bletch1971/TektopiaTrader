package bletch.tektopiabanker.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.ParametersAreNonnullByDefault;

import bletch.tektopiabanker.core.ModDetails;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.tangotek.tektopia.structures.VillageStructureType;

@ParametersAreNonnullByDefault
public class ModUtils {
	
	public static List<Block> getModBlocks() {

		return StreamSupport.stream(Block.REGISTRY.spliterator(), false)
				.filter(b -> b.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID))
				.distinct()
				.sorted((b1, b2) -> b1.getClass().getTypeName().compareTo(b2.getClass().getTypeName()))
				.collect(Collectors.toList());
    }
	
	public static List<ItemStack> getModBlockStacks() {

		return StreamSupport.stream(Block.REGISTRY.spliterator(), false)
				.filter(b -> b.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID))
				.distinct()
				.map(b -> new ItemStack(b))
				.sorted((s1, s2) -> s1.getClass().getTypeName().compareTo(s2.getClass().getTypeName()))
				.collect(Collectors.toList());
    }
	
	public static List<Class<?>> getModBlockClasses() {

		return StreamSupport.stream(Block.REGISTRY.spliterator(), false)
				.filter(b -> b.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID))
				.map(b -> b.getClass())
				.filter(c -> !c.getTypeName().equalsIgnoreCase(Block.class.getTypeName()))
				.distinct()
				.sorted((c1, c2) -> c1.getTypeName().compareTo(c2.getTypeName()))
				.collect(Collectors.toList());
    }
	
	public static List<Item> getModItems() {

		return StreamSupport.stream(Item.REGISTRY.spliterator(), false)
        		.filter(i -> i.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID))
        		.distinct()
        		.sorted((i1, i2) -> i1.getClass().getTypeName().compareTo(i2.getClass().getTypeName()))
        		.collect(Collectors.toList());
    }
	
	public static List<ItemStack> getModItemStacks() {

		return StreamSupport.stream(Item.REGISTRY.spliterator(), false)
        		.filter(i -> i.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID))
        		.distinct()
        		.map(i -> new ItemStack(i))
        		.sorted((s1, s2) -> s1.getClass().getTypeName().compareTo(s2.getClass().getTypeName()))
        		.collect(Collectors.toList());
    }
	
	public static List<Class<?>> getModItemClasses() {

		return StreamSupport.stream(Item.REGISTRY.spliterator(), false)
        		.filter(i -> i.getRegistryName().getResourceDomain().equalsIgnoreCase(ModDetails.MOD_ID))
        		.distinct()
        		.map(i -> i.getClass())
        		.sorted((c1, c2) -> c1.getTypeName().compareTo(c2.getTypeName()))
        		.collect(Collectors.toList());
    }
	
	public static List<VillageStructureType> getHomeStructureTypes() {
		return StreamSupport.stream(Arrays.spliterator(VillageStructureType.values()), false)
				.distinct()
				.filter(t -> t.isHome() || t == VillageStructureType.BARRACKS)
				.sorted((c1, c2) -> c1.name().compareTo(c2.name()))
				.collect(Collectors.toList());
	}
	
}
