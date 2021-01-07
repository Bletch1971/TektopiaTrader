package bletch.tektopiabanker.core;

import bletch.tektopiabanker.models.ModelBankersBell;
import bletch.tektopiabanker.tileentities.TileEntityBankersBell;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {

	public static void register() {
		GameRegistry.registerTileEntity(TileEntityBankersBell.class, ModelBankersBell.modelResourceLocation);
	}

}
