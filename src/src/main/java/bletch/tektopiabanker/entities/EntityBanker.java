package bletch.tektopiabanker.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;

import bletch.common.utils.TextUtils;
import bletch.tektopiabanker.core.ModConfig;
import bletch.tektopiabanker.core.ModDetails;
import bletch.tektopiabanker.core.ModEntities;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.INpc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.tangotek.tektopia.TekVillager;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVendor;
import net.tangotek.tektopia.tickjob.TickJob;

public class EntityBanker extends EntityVendor implements INpc {
	
	public static final String ANIMATION_MODEL_NAME = "banker_m";
	public static final String ENTITY_NAME = "banker";
	public static final String MODEL_NAME = "banker";
	public static final String RESOURCE_PATH = "banker";

	protected static final String NBTTAG_BUYINGLIST = "bankerbuyinglist";

	protected static final DataParameter<String> ANIMATION_KEY;
	protected static final AnimationHandler<EntityBanker> animationHandler;

	public EntityBanker(World worldIn) {
		super(worldIn, 0);
		
		this.sleepOffset = 0;
	}
	
	public String getProfessionName() {
		return TextUtils.translate(this.getTranslationKey());
	}

	@Override
	public boolean isMale() {
		return true;
	}
	
	@Override
	public boolean isLearningTime() {
		return false;
	}

	@Override
	public boolean isSleepingTime() {
		return EntityBanker.isSleepingTime(this.world);
	}

	@Override
	public boolean isWorkTime() {
		return EntityBanker.isWorkTime(this.world);
	}

	@Override
	protected void setupServerJobs() {
		super.setupServerJobs();
		
		this.addJob(new TickJob(50, 0, true, () -> {
			// check if night time, banker needs to sleep :)
			if (ModConfig.banker.dayTimeOnly && !this.isWorkTime()) {
				this.setDead();
			}      
		}));
	}
	
	@Override
	protected void populateBuyingList() {
		if (this.buyingList == null && this.hasVillage()) {
			this.buyingList = new MerchantRecipeList();
			
			List<ItemStack> tradeItems = new ArrayList<ItemStack>();
			
			if (ModConfig.banker.bankerTrades != null && ModConfig.banker.bankerTrades.length > 0) {
				for (String tradeItem : ModConfig.banker.bankerTrades) {
					if (tradeItem == null || tradeItem.trim() == "") {
						continue;
					}
					
					String[] itemParts = tradeItem.split("[*]");
					String itemName = itemParts[0];
					Integer quantity = 1;
					
					if (itemParts.length > 1) {
						try {
							quantity = Integer.parseInt(itemParts[1]);
						}
						catch (NumberFormatException ex) {
							quantity = 1;
						}
					}
					
					Item item = Item.getByNameOrId(itemName);
					if (item != null) {
						tradeItems.add(new ItemStack(item, quantity));
					}
				}
			}
			// check if there are any trade items defined.
			if (tradeItems.size() == 0) {
				// no trade items, set to the defaults.
				tradeItems.add(new ItemStack(Items.IRON_INGOT, 64));
				tradeItems.add(new ItemStack(Items.GOLD_INGOT, 32));
				tradeItems.add(new ItemStack(Items.DIAMOND, 8));
				tradeItems.add(new ItemStack(Blocks.REDSTONE_BLOCK, 16));
				tradeItems.add(new ItemStack(Blocks.LAPIS_BLOCK, 16));
			}
			
			// create the merchant buying list
			for (ItemStack itemStack : tradeItems) {
				if (itemStack == null) {
					continue;
				}
				
				this.buyingList.add(new MerchantRecipe(itemStack, ItemStack.EMPTY, new ItemStack(Items.EMERALD, 1), 0, 99999));
			}
		}
	}
	
	@Override
	protected String getTranslationKey() {
		return "entity." + MODEL_NAME + ".name";
	}

	@Override
	public AnimationHandler<EntityBanker> getAnimationHandler() {
		return animationHandler;
	}
	
	@Override
	public void setCustomer(EntityPlayer player) {
		this.buyingPlayer = player;
		this.buyingList = null;
	}

	@Override
	public EntityPlayer getCustomer() {
		return this.buyingPlayer;
	}

	@Override
	public MerchantRecipeList getRecipes(EntityPlayer player) {
		if (this.buyingList == null) {
			this.populateBuyingList();
		}

		return this.buyingList;
	}

	@Override
	public void setRecipes(MerchantRecipeList recipeList) {
	}

	@Override
	public void useRecipe(MerchantRecipe recipe) {
		super.func_70933_a(recipe);
	}

	@Override
	public void verifySellingItem(ItemStack stack) {
		if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            this.playSound(stack.isEmpty() ? SoundEvents.ENTITY_VILLAGER_NO : SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch());
        }
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public BlockPos getPos() {
		return new BlockPos(this);
	}

	@Override
	protected void crowdingCheck() {
		
	}
	
	@Override
	public void playClientAnimation(String animationName) {
		if (!this.getAnimationHandler().isAnimationActive(ModDetails.MOD_ID, animationName, this)) {
			this.getAnimationHandler().startAnimation(ModDetails.MOD_ID, animationName, this);
		}
	}

	@Override
	public void stopClientAnimation(String animationName) {
		super.stopClientAnimation(animationName);
		if (this.getAnimationHandler().isAnimationActive(ModDetails.MOD_ID, animationName, this)) {
			this.getAnimationHandler().stopAnimation(ModDetails.MOD_ID, animationName, this);
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.dataManager.set(ANIMATION_KEY, "");
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(ANIMATION_KEY, "");
		super.entityInit();
	}

	protected void updateClientAnimation(String animationName) {
		ClientAnimationHandler<EntityBanker> clientAnimationHandler = (ClientAnimationHandler<EntityBanker>)this.getAnimationHandler();
		
		Set<String> animChannels = clientAnimationHandler.getAnimChannels().keySet();
		animChannels.forEach(a -> clientAnimationHandler.stopAnimation(a, this));
		
		if (!animationName.isEmpty()) {
			clientAnimationHandler.startAnimation(ModDetails.MOD_ID, animationName, this);
		}
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		
		if (this.isWorldRemote() && ANIMATION_KEY.equals(key)) {
			this.updateClientAnimation(this.dataManager.get(ANIMATION_KEY));
		}
	}   
	
	@Override
	public void stopServerAnimation(String animationName) {
		this.dataManager.set(ANIMATION_KEY, "");
	}

	@Override
	public void playServerAnimation(String animationName) {
		this.dataManager.set(ANIMATION_KEY, animationName);
	}

	@Override
	public boolean isPlayingAnimation(String animationName) {
		return animationName == this.dataManager.get(ANIMATION_KEY);
	}
	
	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		return super.func_180482_a(difficulty, livingdata);
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.func_70037_a(compound);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.func_70014_b(compound);
	}
	
	static {
		ANIMATION_KEY = EntityDataManager.createKey(EntityBanker.class, DataSerializers.STRING);
		
		animationHandler = TekVillager.getNewAnimationHandler(EntityBanker.class);
		setupCraftStudioAnimations(animationHandler, ANIMATION_MODEL_NAME);
	}
	
	protected static void setupCraftStudioAnimations(AnimationHandler<EntityBanker> animationHandler, String modelName) {
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_EAT, modelName, true);
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_READ, modelName, true);
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_RUN, modelName, true);
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_SIT, modelName, true);
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_SITCHEER, modelName, true);
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_SLEEP, modelName, true);	
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_WALK, modelName, true);
		animationHandler.addAnim(ModDetails.MOD_ID, ModEntities.ANIMATION_VILLAGER_WALKSAD, modelName, true);
	}

	public static boolean isSleepingTime(World world) {
		return Village.isTimeOfDay(world, (long)(SLEEP_START_TIME), (long)(SLEEP_END_TIME));
	}

	public static boolean isWorkTime(World world) {
		return Village.isTimeOfDay(world, (long)WORK_START_TIME, (long)WORK_END_TIME, (long)0);
	}

}
