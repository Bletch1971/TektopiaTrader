package bletch.tektopiabanker.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import bletch.tektopiabanker.blocks.BlockBankersBell;
import bletch.tektopiabanker.core.ModDetails;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJModel.OBJProperty;
import net.minecraftforge.client.model.obj.OBJModel.OBJState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

@SuppressWarnings("deprecation")
public class ModelBankersBell implements IBakedModel {
	public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(ModDetails.MOD_ID + ":bankersbell");
	public static final ResourceLocation objResourceLocation = new ResourceLocation(ModDetails.MOD_ID + ":block/bankersbell.obj");

	private IModel model = null;
	private IBakedModel bakedModel;
	private IBakedModel overrideModel;
	private ModelItemOverrideList overrideList = new ModelItemOverrideList();
	
	public ModelBankersBell() {
		this.overrideModel = this;
	}
	
	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		this.getModel(state, 0);
		
		try {
			return this.bakedModel.getQuads(state, side, rand);
		} 
		catch (NullPointerException ex) {
			return new ArrayList<BakedQuad>();
		}
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}
	   
	@Override
	public ItemOverrideList getOverrides() {
		return this.overrideList;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return this.bakedModel.getParticleTexture() == null 
				? Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(ModDetails.MOD_ID_MINECRAFT + ":blocks/planks_oak") 
				: this.bakedModel.getParticleTexture();
	}

	@Override
	public Pair<ModelBankersBell, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		TRSRTransformation transform = new TRSRTransformation(new Vector3f(0.0F, 0.0F, 0.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F));
		
		switch(cameraTransformType) {
		case FIRST_PERSON_RIGHT_HAND:
			transform = new TRSRTransformation(new Vector3f(1.4F, 0.2F, -1.5F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F));
			// tweak transformation
			break;
		case FIRST_PERSON_LEFT_HAND:
			transform = new TRSRTransformation(new Vector3f(1.4F, 0.2F, 0.5F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F));
			// tweak transformation
			transform = transform.compose(new TRSRTransformation(new Vector3f(-0.0F, -0.0F, -2.0F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F)));
			break;
		case THIRD_PERSON_RIGHT_HAND:
			transform = new TRSRTransformation(new Vector3f(0.6F, 0.8F, 0.25F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(0.65F, 0.65F, 0.65F), new Quat4f(1.0F, 0.0F, 0.0F, 1.0F));
			transform = transform.compose(new TRSRTransformation(new Vector3f(0.0F, 0.0F, 0.0F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F)));
			// tweak transformation
			break;
		case THIRD_PERSON_LEFT_HAND:
			transform = new TRSRTransformation(new Vector3f(0.65F, -0.5F, 0.25F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(0.65F, 0.65F, 0.65F), new Quat4f(1.0F, 0.0F, 0.0F, 1.0F));
			transform = transform.compose(new TRSRTransformation(new Vector3f(0.0F, 0.0F, 0.0F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F)));
			// tweak transformation
			transform = transform.compose(new TRSRTransformation(new Vector3f(-0.0F, -0.0F, -2.0F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 1.0F, 0.0F, 1.0F)));
			break;
		case GUI:
			transform = new TRSRTransformation(new Vector3f(0.0F, 0.26F, 0.0F), new Quat4f(0.25F, 1.0F, 0.25F, 1.0F), new Vector3f(0.75F, 0.75F, 0.75F), new Quat4f(0.0F, 0.4F, 0.0F, 1.0F));
			// tweak transformation
			transform = transform.compose(new TRSRTransformation(new Vector3f(0.0F, 1.85F, 0.09F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(2.0F, 2.0F, 2.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F)));
			break;
		case GROUND:
			transform = new TRSRTransformation(new Vector3f(0.5F, 0.05F, 0.5F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(0.5F, 0.5F, 0.5F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F));
			break;
		case FIXED:
			transform = new TRSRTransformation(new Vector3f(-0.75F, 0.12F, 0.75F), new Quat4f(0.0F, -1.0F, 0.0F, 1.0F), new Vector3f(0.75F, 0.75F, 0.75F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F));
			break;
		case NONE:
			transform = new TRSRTransformation(new Vector3f(0.0F, 0.0F, 0.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F));
		default:
			break;
		}

		// tweak master transformation
		transform = transform.compose(new TRSRTransformation(new Vector3f(-1.0F, 0.5F, -1.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(2.0F, 2.0F, 2.0F), new Quat4f(0.0F, 0.0F, 0.0F, 1.0F)));
		return Pair.of(this, transform.getMatrix());
	}
	
	private void getModel(IBlockState state, int attempt) {
		
		if (this.model == null || this.model != null && !this.model.toString().contains("obj.OBJModel")) {
			try {
				this.model = ModelLoaderRegistry.getModel(objResourceLocation);
				this.model = this.model.process(ImmutableMap.of("flip-v", "true"));
			} 
			catch (Exception ex) {
				this.model = ModelLoaderRegistry.getMissingModel();
				
				if (attempt <= 5) {
					this.getModel(state, attempt + 1);
					return;
				}
			}
		}

		List<String> modelParts = Lists.newArrayList(new String[]{"OBJModel.Group.All.Key"});
		OBJState modelState = new OBJState(modelParts, true);
		
		if (state != null && state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState)state;
			if (extendedState.getUnlistedNames().contains(OBJProperty.INSTANCE)) {
				modelState = (OBJState)extendedState.getValue(OBJProperty.INSTANCE);
			}

			if (modelState == null) {
				return;
			}
		}

		this.bakedModel = this.model.bake(modelState, DefaultVertexFormats.ITEM, new Function<ResourceLocation, TextureAtlasSprite>() {
			@Override
			public TextureAtlasSprite apply(ResourceLocation location) {
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
			}
		});
	}
	
	private class ModelItemOverrideList extends ItemOverrideList {
		
		public ModelItemOverrideList() {
			super(ImmutableList.of());
		}
		
		@Nonnull
		public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
			ModelBankersBell.this.getModel((IBlockState)null, 0);
			return ModelBankersBell.this.overrideModel;
		}
		
	}
	
	public static class BankersBellStateMapper extends DefaultStateMapper {
		
		public static final BankersBellStateMapper instance = new BankersBellStateMapper();
		
		@Override
		public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
			Map<IBlockState, ModelResourceLocation> modelLocations = Maps.<IBlockState, ModelResourceLocation>newLinkedHashMap();
			
			if (blockIn instanceof BlockBankersBell) {
				BlockBankersBell block = (BlockBankersBell)blockIn;
				IBlockState state = block.getDefaultState();
				modelLocations.put(state, ModelBankersBell.modelResourceLocation);
			}
			
			return modelLocations;
		}
		
	}
	
}
