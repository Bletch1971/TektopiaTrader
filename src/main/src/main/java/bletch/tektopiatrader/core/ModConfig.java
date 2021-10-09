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
	
	@Config.LangKey("config.debug")
	public static final Debug debug = new Debug();
	
	@Config.LangKey("config.trader")
	public static final Trader trader = new Trader();
	
	public static class Debug {
		
		@Config.Comment("If true, debug information will be output to the console.")
		@Config.LangKey("config.debug.enableDebug")
		public boolean enableDebug = false;
		
	}
	
	public static class Trader {
		
		@Config.Comment("The list of trade items for the trader, each trade item is worth 1 emerald. Must be in the format <modid>:<item/block>*<quantity> (Eg. minecraft:iron_ingot*64)")
		@Config.LangKey("config.trader.trades")
		public String[] trades = new String[] {"minecraft:iron_ingot*64", "minecraft:gold_ingot*32", "minecraft:diamond*8", "minecraft:redstone_block*16", "minecraft:lapis_block*16"};
	
	}
	
}
