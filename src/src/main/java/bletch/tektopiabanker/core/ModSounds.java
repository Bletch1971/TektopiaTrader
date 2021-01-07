package bletch.tektopiabanker.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(ModDetails.MOD_ID)
public class ModSounds {
	
	public static SoundEvent BellRing = createSoundEvent("bell_ring");
	
	public static void register(IForgeRegistry<SoundEvent> registry) {
		registry.register(BellRing);
	}
	
    private static SoundEvent createSoundEvent(String soundName) {
        ResourceLocation soundResource = new ResourceLocation(ModDetails.MOD_ID, soundName);
        return new SoundEvent(soundResource).setRegistryName(soundResource);
    }
    
}
