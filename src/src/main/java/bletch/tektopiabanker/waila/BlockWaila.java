package bletch.tektopiabanker.waila;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import bletch.common.utils.TextUtils;
import bletch.tektopiabanker.core.ModConfig;
import bletch.tektopiabanker.utils.DebugUtils;
import bletch.tektopiabanker.utils.ModUtils;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public class BlockWaila implements IWailaDataProvider {

	public static final String KEY_SUFFIX_DESCRIPTION = ".description";
	public static final String KEY_SUFFIX_TOOLTIP = ".tooltip";
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if (ModConfig.waila.useSneaking && !accessor.getPlayer().isSneaking()) {
			return currentTip;
		}
		
		if (ModConfig.waila.blocks.showBlockTooltip) {
			String translateKey_tooltip = itemStack.getUnlocalizedName() + KEY_SUFFIX_TOOLTIP;
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaBlockTranslationKey) {
				currentTip.add(TextUtils.translate("gui.translationkey") + " " + translateKey_tooltip);
			}
			
			List<String> value = TextUtils.translateMulti(translateKey_tooltip);
			if (value != null && value.size() > 0) {
				currentTip.addAll(value);
			}
		}
		
		if (ModConfig.waila.blocks.showBlockInformation) {
			String translateKey_description = itemStack.getUnlocalizedName() + KEY_SUFFIX_DESCRIPTION;
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaBlockTranslationKey) {
				currentTip.add(TextUtils.translate("gui.translationkey") + " " + translateKey_description);
			}
			
			List<String> value = TextUtils.translateMulti(translateKey_description);
			if (value != null && value.size() > 0) {
				currentTip.addAll(value);
			}
		}
    	
		return currentTip;
	}
	
	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return currentTip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity tileEntity, NBTTagCompound tag, World world, BlockPos position) {
		if (tileEntity != null) {
			tileEntity.writeToNBT(tag);
		}

		return tag;
	}

	public static void callbackRegister(IWailaRegistrar registrar) {
		BlockWaila dataProvider = new BlockWaila();
		ArrayList<String> processed = new ArrayList<String>();
		
		ArrayList<Class<?>> modClasses = new ArrayList<Class<?>>();
		modClasses.addAll(ModUtils.getModBlockClasses());
		
		// remove any blocks that are inherited from other mod blocks
		for (int i = modClasses.size() - 1; i >= 0; i--) {
			if (modClasses.contains(modClasses.get(i).getSuperclass())) {
				modClasses.remove(i);
			}
		}
		
		for (Class<?> modClass : modClasses) {
        	String key = modClass.getTypeName();

        	if (processed.contains(key)) {
        		continue;
        	}
        	processed.add(key);
        	
        	registrar.registerNBTProvider(dataProvider, modClass);
			registrar.registerBodyProvider(dataProvider, modClass);
			
			if (ModConfig.debug.enableDebug && ModConfig.debug.showWailaBlocksRegistered) {
				DebugUtils.writeLine("Registered WAILA information for block " + key, true);
			} 
		}	
	}

}
