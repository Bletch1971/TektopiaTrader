package bletch.tektopiabanker.core;

import java.io.File;

import javax.annotation.ParametersAreNonnullByDefault;

import com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper;
import com.leviathanstudio.craftstudio.client.util.EnumRenderType;
import com.leviathanstudio.craftstudio.client.util.EnumResourceType;

import bletch.tektopiabanker.entities.EntityBanker;
import bletch.tektopiabanker.tooltips.ItemTooltip;
import bletch.tektopiabanker.top.BlockTop;
import bletch.tektopiabanker.top.EntityTop;
import bletch.tektopiabanker.waila.BlockWaila;
import bletch.tektopiabanker.waila.EntityWaila;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@ParametersAreNonnullByDefault
public class ModClientProxy extends ModCommonProxy {

	protected CSRegistryHelper registry = new CSRegistryHelper(ModDetails.MOD_ID);

	@Override
	public boolean isRemote() {
		return true;
	}

	@Override
	public File getMinecraftDirectory() {
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public void preInitialize(FMLPreInitializationEvent e) {
		super.preInitialize(e);
		
		OBJLoader.INSTANCE.addDomain(ModDetails.MOD_ID);
	}
	
	public void initialize(FMLInitializationEvent e) {
		super.initialize(e);
	}

	public void postInitialize(FMLPostInitializationEvent e) {
		super.postInitialize(e);
	}
	   
	public void registerCraftStudioAnimations() {
		super.registerCraftStudioAnimations();
		
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_EAT);
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_READ);
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_RUN);
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_SIT);
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_SITCHEER);
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_SLEEP);
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_WALK);
		this.registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, ModEntities.ANIMATION_VILLAGER_WALKSAD);
	}	
	
	public void registerCraftStudioModels() {
		super.registerCraftStudioModels();
		
		registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, EntityBanker.ANIMATION_MODEL_NAME);
	}
	
	@Override
	public void registerTheOneProbe() {
		super.registerTheOneProbe();
		
		if (Loader.isModLoaded(ModDetails.MOD_ID_TOP) && ModConfig.top.enableTopIntegration) {
			ModDetails.MOD_LOGGER.info("Registering blocks with The One Probe");
			FMLInterModComms.sendFunctionMessage(ModDetails.MOD_ID_TOP, "getTheOneProbe", BlockTop.class.getTypeName() + "$getTheOneProbe");
			ModDetails.MOD_LOGGER.info("Registered blocks with The One Probe");
			
			ModDetails.MOD_LOGGER.info("Registering entities with The One Probe");
			FMLInterModComms.sendFunctionMessage(ModDetails.MOD_ID_TOP, "getTheOneProbe", EntityTop.class.getTypeName() + "$getTheOneProbe");
			ModDetails.MOD_LOGGER.info("Registered entities with The One Probe");
		}
	}
	
	@Override
	public void registerTooltips() {
		super.registerTooltips();
		
    	if (ModConfig.tooltips.enableTooltipIntegration) {
	    	ModDetails.MOD_LOGGER.info("Registering Item Tooltip");
			MinecraftForge.EVENT_BUS.register(new ItemTooltip());
			ModDetails.MOD_LOGGER.info("Registered Item Tooltip");
    	}
	}
	
	public void registerWaila() {
		super.registerWaila();
		
		if (Loader.isModLoaded(ModDetails.MOD_ID_WAILA) && ModConfig.waila.enableWailaIntegration) {
			ModDetails.MOD_LOGGER.info("Registering blocks with Waila");
			FMLInterModComms.sendMessage(ModDetails.MOD_ID_WAILA, "register", BlockWaila.class.getTypeName() + ".callbackRegister");
			ModDetails.MOD_LOGGER.info("Registered blocks with Waila");
			
			ModDetails.MOD_LOGGER.info("Registering entities with Waila");
			FMLInterModComms.sendMessage(ModDetails.MOD_ID_WAILA, "register", EntityWaila.class.getTypeName() + ".callbackRegister");
			ModDetails.MOD_LOGGER.info("Registered entities with Waila");
		}
	}
		
}
