package bletch.tektopiabanker.entities.render;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;

import bletch.tektopiabanker.core.ModDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.tangotek.tektopia.client.LayerVillagerHeldItem;
import net.tangotek.tektopia.entities.EntityVillagerTek;

public class RenderVillager<T extends EntityVillagerTek> extends RenderLiving<T> {
	protected final String textureName;
	protected final ModelCraftStudio maleModel;
	protected final ModelCraftStudio femaleModel;
	protected ResourceLocation[] maleTextures;
	protected ResourceLocation[] femaleTextures;
	
	public RenderVillager(RenderManager manager, String modelName, boolean hasGenderModels, int textureWidth, int textureHeight, String textureName) {
		this(manager, modelName, hasGenderModels, textureWidth, textureHeight, textureName, 0.4F);
	}
	
	public RenderVillager(RenderManager manager, String modelName, boolean hasGenderModels, int textureWidth, int textureHeight, String textureName, float shadowSize) {
		super(manager, new ModelCraftStudio(ModDetails.MOD_ID, modelName + "_m", textureWidth, textureHeight), shadowSize);
		
		this.addLayer(new LayerVillagerHeldItem(this));
		this.textureName = textureName;
		this.maleModel = (ModelCraftStudio)this.mainModel;
		if (hasGenderModels) {
			this.femaleModel = new ModelCraftStudio(ModDetails.MOD_ID, modelName + "_f", textureWidth, textureHeight);
		} else {
			this.femaleModel = null;
		}
		
		this.setupTextures();
	}
	
	protected void setupTextures() {
		this.maleTextures = new ResourceLocation[] { new ResourceLocation(ModDetails.MOD_ID, "textures/entity/" + this.textureName + "_m.png") };
		this.femaleTextures = new ResourceLocation[] { new ResourceLocation(ModDetails.MOD_ID, "textures/entity/" + this.textureName + "_f.png") };
	}
	
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if (!entity.isMale() && this.femaleModel != null) {
			this.mainModel = this.femaleModel;
		} else {
			this.mainModel = this.maleModel;
		}
		
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	protected ResourceLocation getEntityTexture(T entity) {
		return entity.isMale() ? this.maleTextures[0] : this.femaleTextures[0];
	}
	
	protected void applyRotations(EntityVillagerTek entityLiving, float p_77043_2_, float rotationYaw, float partialTicks) {
		if (entityLiving.getForceAxis() >= 0) {
			GlStateManager.rotate((float)(entityLiving.getForceAxis() * -90), 0.0F, 1.0F, 0.0F);
		} else {
			super.applyRotations((T)entityLiving, p_77043_2_, rotationYaw, partialTicks);
		}
	}
	
}
