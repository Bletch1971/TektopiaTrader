package bletch.tektopiabanker.entities.render;

import bletch.tektopiabanker.entities.EntityBanker;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderBanker<T extends EntityBanker> extends RenderVillager<T> {
	public static final RenderBanker.Factory FACTORY;
	
	public RenderBanker(RenderManager manager) {
		super(manager, EntityBanker.MODEL_NAME, false, 64, 64, EntityBanker.MODEL_NAME);
	}
	
	public static class Factory<T extends EntityBanker> implements IRenderFactory<T>
	{
		public Render<? super T> createRenderFor(RenderManager manager) {
			return (Render<? super T>)new RenderBanker(manager);
		}
	}
	
    static {
        FACTORY = new RenderBanker.Factory();
    }
    
}
