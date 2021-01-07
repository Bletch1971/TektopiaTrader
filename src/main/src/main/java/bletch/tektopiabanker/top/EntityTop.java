package bletch.tektopiabanker.top;

import bletch.common.utils.TextUtils;
import bletch.tektopiabanker.core.ModConfig;
import bletch.tektopiabanker.core.ModDetails;
import bletch.tektopiabanker.entities.EntityBanker;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class EntityTop {
	
	public static class getTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		public static ITheOneProbe probe;

		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			probe = theOneProbe;
			
			probe.registerEntityProvider(new IProbeInfoEntityProvider() {
				
				@Override
				public String getID() {
					return ModDetails.MOD_ID + ":default";
				}

				@Override
				public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
					
					if (entity instanceof EntityVillagerTek) {
						EntityTop.getTheOneProbe.addEntityVillagerTekInfo(mode, probeInfo, player, world, (EntityVillagerTek)entity, data);
					}	
					
				}
				
			});
			
			return null;
		}

		private static boolean addEntityVillagerTekInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, EntityVillagerTek entity, IProbeHitEntityData data) {

			if (ModConfig.top.useSneaking && mode == ProbeMode.NORMAL) {
				return false;
			}
			
			EntityVillagerTek villager = entity;
			String output = "";
			
			if (villager != null) {
				boolean showVillageDetails = false;
				
				if (villager instanceof EntityBanker) {
					showVillageDetails = true;
				}
				
				if (showVillageDetails) {
					// Village
					Village village = villager.getVillage();
					if (village != null) {
						
						String villageName = village.getName();
						output = TextUtils.translate("gui.villager.villagename") + " " + TextFormatting.WHITE + villageName;
						probeInfo.text(TextFormatting.DARK_AQUA + output);
						
						// has bed
						boolean hasBed = villager.getBedPos() != null;
						output = TextUtils.translate("gui.villager.hasbed") + " " + TextFormatting.WHITE + (hasBed ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
						probeInfo.text(TextFormatting.DARK_AQUA + output);					
					}					
				}
				
				return true;
			}

			return false;
		}
	}
}
