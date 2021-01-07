package bletch.tektopiabanker.core;

import bletch.tektopiabanker.models.ModelBankersBell;
import bletch.tektopiabanker.models.ModelBankersBell.BankersBellStateMapper;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModRenderers {

	@SideOnly(Side.CLIENT)
	public static void register() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.BankersBell), 0, ModelBankersBell.modelResourceLocation);
        ModelLoader.setCustomStateMapper(ModBlocks.BankersBell, BankersBellStateMapper.instance);
	}
	
}
