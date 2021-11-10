package bletch.tektopiatrader.entities.render;

import bletch.tektopiatrader.entities.EntityTrader;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderTrader<T extends EntityTrader> extends RenderVillager<T> {
    public static final RenderTrader.Factory<EntityTrader> FACTORY;

    public RenderTrader(RenderManager manager) {
        super(manager, EntityTrader.MODEL_NAME, false, 128, 64, EntityTrader.MODEL_NAME);
    }

    public static class Factory<T extends EntityTrader> implements IRenderFactory<T> {
        public Render<? super T> createRenderFor(RenderManager manager) {
            return new RenderTrader<EntityTrader>(manager);
        }
    }

    static {
        FACTORY = new RenderTrader.Factory<>();
    }

}
