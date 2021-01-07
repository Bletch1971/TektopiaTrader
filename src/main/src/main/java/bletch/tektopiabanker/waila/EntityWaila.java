package bletch.tektopiabanker.waila;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import bletch.common.utils.StringUtils;
import bletch.common.utils.TextUtils;
import bletch.tektopiabanker.core.ModConfig;
import bletch.tektopiabanker.entities.EntityBanker;
import bletch.tektopiabanker.utils.DebugUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;

@ParametersAreNonnullByDefault
public class EntityWaila implements IWailaEntityProvider {
	
	private static String NBT_TAG_BEDPOSITION = "bedPosition";
	private static String NBT_TAG_DAYSALIVE = "daysAlive";
	private static String NBT_TAG_HASVILLAGE = "hasVillage";
	private static String NBT_TAG_VILLAGENAME = "villageName";
	
	@Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return accessor.getEntity();
    }

	@Override
	public List<String> getWailaHead(Entity entity, List<String> currentTip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		
		if (entity instanceof EntityVillagerTek) {
			EntityVillagerTek villager = ((EntityVillagerTek)entity);
			
			if (villager != null) {
				
				String tooltip = TextFormatting.RESET.toString();
				
				if (entity instanceof EntityBanker) {
					// add villager name
					String villagerName = villager.getName();
					tooltip += TextFormatting.WHITE + villagerName;
				}
				
				// add villager gender
				if (villager.isMale()) {
					tooltip += " " + TextUtils.SYMBOL_MALE;
				} else {
					tooltip += " " + TextUtils.SYMBOL_FEMALE;
				}
				
				if (!StringUtils.isNullOrWhitespace(tooltip) && !tooltip.equalsIgnoreCase(TextFormatting.RESET.toString())) {
					if (currentTip.size() > 0) {
						currentTip.set(0, tooltip);
					} else {
						currentTip.add(tooltip);
					}
				} else {
					if (currentTip.size() > 0) {
						currentTip.remove(0);
					}
				}
			}
		}
		
		return currentTip;
	}

	@Override
	public List<String> getWailaBody(Entity entity, List<String> currentTip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {

		if (ModConfig.waila.useSneaking && !accessor.getPlayer().isSneaking()) {
			return currentTip;
		}
				
		if (!ModConfig.waila.entities.showEntityInformation) {
			return currentTip;
		}
		
		NBTTagCompound tag = accessor.getNBTData();
		String output = "";
		
		if (entity instanceof EntityVillagerTek) {
			EntityVillagerTek villager = ((EntityVillagerTek)entity);
			
			if (villager != null) {
				boolean showVillageDetails = false;
				
				if (entity instanceof EntityBanker) {
					// Profession
					String profession = ((EntityBanker)entity).getProfessionName();
					output = profession;
					currentTip.add(TextFormatting.DARK_AQUA + output);			
					
					// Health
					float health = villager.getHealth();
					output = TextUtils.translate("gui.villager.health") + " " + TextFormatting.WHITE + health;
					currentTip.add(TextFormatting.DARK_AQUA + output);
					
					showVillageDetails = true;
				}
				
				if (showVillageDetails) {
					// Village
					Village village = villager.getVillage();
					
					if (village != null) {
						
						String villageName = village.getName();
						output = TextUtils.translate("gui.villager.villagename") + " " + TextFormatting.WHITE + villageName;
						currentTip.add(TextFormatting.DARK_AQUA + output);
						
						// has bed
						boolean hasBed = villager.getBedPos() != null;
						output = TextUtils.translate("gui.villager.hasbed") + " " + TextFormatting.WHITE + (hasBed ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
						currentTip.add(TextFormatting.DARK_AQUA + output);	
						
					} else {
		
						if (tag.hasKey(NBT_TAG_HASVILLAGE)) {
							
							String villageName = tag.hasKey(NBT_TAG_VILLAGENAME) ? tag.getString(NBT_TAG_VILLAGENAME) : "";
							output = TextUtils.translate("gui.villager.villagename") + " " + TextFormatting.WHITE + villageName;
							currentTip.add(TextFormatting.DARK_AQUA + output);
							
							if (tag.hasKey(NBT_TAG_BEDPOSITION)) {
								boolean hasBed = true;
								output = TextUtils.translate("gui.villager.hasbed") + " " + TextFormatting.WHITE + (hasBed ? TextUtils.SYMBOL_GREENTICK : TextUtils.SYMBOL_REDCROSS);
								currentTip.add(TextFormatting.DARK_AQUA + output);
							}
						}
					}
				}
			}
		}
		
		return currentTip;
	}
	
	@Override
	public List<String> getWailaTail(Entity entity, List<String> currentTip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, Entity entity, NBTTagCompound tag, World world) {
		if (entity != null) {
			entity.writeToNBT(tag);
		}
		
		if (entity instanceof EntityVillagerTek) {
			tag.setInteger(NBT_TAG_DAYSALIVE, ((EntityVillagerTek)entity).getDaysAlive());			
			tag.setBoolean(NBT_TAG_HASVILLAGE, ((EntityVillagerTek)entity).hasVillage());
			
			if (((EntityVillagerTek)entity).hasVillage()) {
				Village village = ((EntityVillagerTek)entity).getVillage();
				tag.setString(NBT_TAG_VILLAGENAME, village.getName());
				
				if (((EntityVillagerTek)entity).getBedPos() != null) {
					tag.setLong(NBT_TAG_BEDPOSITION, ((EntityVillagerTek)entity).getBedPos().toLong());
				}
			}
		}
		
		return tag;
	}

	public static void callbackRegister(IWailaRegistrar registrar) {
		EntityWaila entityProvider = new EntityWaila();
		ArrayList<String> processed = new ArrayList<String>();
		
		for (Class<?> entity : getTektopiaEntities()) {
        	String key = entity.getTypeName();
        	
        	if (processed.contains(key)) {
        		continue;
        	}
        	processed.add(key);
        	
        	registrar.registerNBTProvider(entityProvider, entity);
        	
        	registrar.registerHeadProvider(entityProvider, entity);
			registrar.registerBodyProvider(entityProvider, entity);
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaEntitiesRegistered) {
				DebugUtils.writeLine("Registered WAILA information for entity " + key, true);
			} 
		}
	}
	
	private static ArrayList<Class<?>> getTektopiaEntities() {	
		ArrayList<Class<?>> list = new ArrayList<Class<?>>();
		
		list.add(EntityBanker.class);
		
		// remove any entities that are inherited from other tektopia mod entities
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.contains(list.get(i).getSuperclass())) {
				list.remove(i);
			}
		}
		
		list.sort((c1, c2) -> c1.getTypeName().compareTo(c2.getTypeName()));
		list.trimToSize();
		
		return list;
	}
}
