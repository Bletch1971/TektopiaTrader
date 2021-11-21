package bletch.tektopiatrader.core;

import bletch.common.core.CommonEntities;
import bletch.tektopiatrader.entities.EntityTrader;
import bletch.tektopiatrader.entities.render.RenderTrader;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class ModEntities extends CommonEntities {

    public static void register(IForgeRegistry<EntityEntry> registry) {
        int id = 1;

        registry.register(EntityEntryBuilder.create()
                .entity(EntityTrader.class)
                .id(new ResourceLocation(ModDetails.MOD_ID, EntityTrader.RESOURCE_PATH), id++)
                .name(EntityTrader.ENTITY_NAME)
                .egg(2697513, 7494986)
                .tracker(128, 1, true)
                .build()
        );
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTrader.class, RenderTrader.FACTORY);
    }

}
