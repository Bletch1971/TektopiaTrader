package bletch.tektopiabanker;

import javax.annotation.ParametersAreNonnullByDefault;

import com.leviathanstudio.craftstudio.client.registry.CraftStudioLoader;

import bletch.tektopiabanker.core.ModBlocks;
import bletch.tektopiabanker.core.ModCommonProxy;
import bletch.tektopiabanker.core.ModDetails;
import bletch.tektopiabanker.core.ModEntities;
import bletch.tektopiabanker.core.ModRenderers;
import bletch.tektopiabanker.core.ModSounds;
import bletch.tektopiabanker.core.ModTileEntities;
import bletch.tektopiabanker.models.ModelBankersBell;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid=ModDetails.MOD_ID, name=ModDetails.MOD_NAME, version=ModDetails.MOD_VERSION, dependencies=ModDetails.MOD_DEPENDENCIES, acceptableRemoteVersions="*", acceptedMinecraftVersions="[1.12.2]", updateJSON=ModDetails.MOD_UPDATE_URL)
@ParametersAreNonnullByDefault
public class TektopiaBanker {

	@Instance(ModDetails.MOD_ID)
	public static TektopiaBanker instance;

	@SidedProxy(clientSide = ModDetails.MOD_CLIENT_PROXY_CLASS, serverSide = ModDetails.MOD_SERVER_PROXY_CLASS)
	public static ModCommonProxy proxy;
	   
	@Mod.EventHandler
	public void preInitialize(FMLPreInitializationEvent e) {
		instance = this;
		
		proxy.preInitialize(e);
		proxy.resetDebug();
	}
	  
	@Mod.EventHandler
	public void initialize(FMLInitializationEvent e) {
		proxy.initialize(e);
		
		proxy.registerTooltips();
		proxy.registerWaila();
		proxy.registerTheOneProbe();
	}
	  
	@Mod.EventHandler
	public void postInitialize(FMLPostInitializationEvent e) {
		proxy.postInitialize(e);
	}
    
    @EventBusSubscriber
    public static class RegistrationHandler {

        @CraftStudioLoader
        public static void registerCraftStudio() {
        	proxy.registerCraftStudioModels();
        	proxy.registerCraftStudioAnimations();
        }

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
        	ModBlocks.register(event.getRegistry());
        	ModTileEntities.register();
        }

    	@SubscribeEvent
    	public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
    		ModEntities.register(event.getRegistry());
    	}

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
        	ModBlocks.registerItemBlocks(event.getRegistry());
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void registerModels(ModelRegistryEvent event) {
        	ModEntities.registerModels();
        	ModRenderers.register();
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        	ModSounds.register(event.getRegistry());
        }
        
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onModelBakeEvent(ModelBakeEvent event) {
        	event.getModelRegistry().putObject(ModelBankersBell.modelResourceLocation, new ModelBankersBell());
        }
        
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onTextureStichEvent(TextureStitchEvent event) {
        	event.getMap().registerSprite(new ResourceLocation(ModDetails.MOD_ID + ":models/bankersbell"));
        }
    	
    }
    
}
