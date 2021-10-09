package bletch.tektopiatrader.core;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid=ModDetails.MOD_ID, category="")
@ParametersAreNonnullByDefault
public class ModConfig {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event) {
		
		if (event.getModID().equals(ModDetails.MOD_ID)) {
			ConfigManager.sync(ModDetails.MOD_ID, Type.INSTANCE);
		}
		
	}
	
	@Config.LangKey("config.trader")
	public static final Trader trader = new Trader();
	
	public static class Trader {
		
		@Config.Comment("The list of trade items for the trader, each trade item is worth 1 emerald. Must be in the format <modid>:<item/block>*<quantity> (Eg. minecraft:iron_ingot*64)")
		@Config.LangKey("config.trader.trades")
		public String[] trades = new String[] {"minecraft:iron_ingot*64", "minecraft:gold_ingot*32", "minecraft:diamond*8", "minecraft:redstone_block*8", "minecraft:lapis_block*8"};

		@Config.Comment("The number of trades that can be made for each item per day. Default: 5")
		@Config.LangKey("config.trader.tradesperday")
		@Config.RangeInt(min = 1, max = 99999)
		public int tradesPerDay = 5;
		
		@Config.Comment("If enabled, when trying to spawn a trader it will check the size of the village. The more villagers the less often the trader will spawn. Default: True")
		@Config.LangKey("config.trader.checksvillagesize")
		public Boolean checksVillageSize = true;
	}
	
}
