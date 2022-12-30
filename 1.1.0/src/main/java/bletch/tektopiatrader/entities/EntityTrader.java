package bletch.tektopiatrader.entities;

import bletch.common.entities.EntityVendorBase;
import bletch.common.entities.ai.EntityAILeaveVillage;
import bletch.common.entities.ai.EntityAIVisitVillage;
import bletch.common.entities.ai.EntityAIWanderVillage;
import bletch.common.utils.StringUtils;
import bletch.tektopiatrader.core.ModConfig;
import bletch.tektopiatrader.core.ModDetails;
import bletch.tektopiatrader.utils.LoggerUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.tickjob.TickJob;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class EntityTrader extends EntityVendorBase {

    public static final String ENTITY_NAME = "trader";
    public static final String MODEL_NAME = "trader";
    public static final String RESOURCE_PATH = "trader";
    public static final String ANIMATION_MODEL_NAME = MODEL_NAME + "_m";

    public EntityTrader(World worldIn) {
        super(worldIn, ModDetails.MOD_ID);
    }

    @Override
    public void attachToVillage(Village village) {
        super.attachToVillage(village);

        LoggerUtils.instance.info("Attaching to village", true);
    }

    @Override
    protected void checkStuck() {
        if (this.firstCheck.distanceSq(this.getPos()) < 20.0D) {
            LoggerUtils.instance.info("Killing self...failed to find a way to the village.", true);
            this.setDead();
        }
    }

    @Override
    protected void detachVillage() {
        super.detachVillage();

        LoggerUtils.instance.info("Detaching from village", true);
    }

    @Override
    public ITextComponent getDisplayName() {
        ITextComponent itextcomponent = new TextComponentTranslation("entity." + MODEL_NAME + ".name");
        itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
        itextcomponent.getStyle().setInsertion(this.getCachedUniqueIdString());
        return itextcomponent;
    }

    @Override
    protected void populateBuyingList() {
        if (this.vendorList == null && this.hasVillage()) {
            this.vendorList = new MerchantRecipeList();

            List<ItemStack> itemStackList = new ArrayList<>();

            if (ModConfig.trader.trades != null && ModConfig.trader.trades.length > 0) {
                for (String tradeItem : ModConfig.trader.trades) {
            		if (StringUtils.isNullOrWhitespace(tradeItem)) {
                        continue;
                    }

                    String[] itemParts = tradeItem.split("[*]");
                    String itemName = itemParts[0];
                    int quantity = 1;

                    if (itemParts.length > 1) {
                        try {
                            quantity = Integer.parseInt(itemParts[1]);
                        } catch (NumberFormatException ex) {
                            quantity = 1;
                        }
                    }

                    Item item = Item.getByNameOrId(itemName);
                    if (item != null) {
                        itemStackList.add(new ItemStack(item, quantity));
                    }
                }
            }

            if (itemStackList.size() == 0) {
                itemStackList.add(new ItemStack(Items.IRON_INGOT, 64));
                itemStackList.add(new ItemStack(Items.GOLD_INGOT, 32));
                itemStackList.add(new ItemStack(Items.DIAMOND, 8));
                itemStackList.add(new ItemStack(Blocks.REDSTONE_BLOCK, 8));
                itemStackList.add(new ItemStack(Blocks.LAPIS_BLOCK, 8));
            }

            int tradesPerDay = Math.max(1, Math.min(99999, ModConfig.trader.tradesPerDay));

            // create the merchant recipe list
            for (ItemStack itemStack : itemStackList) {
                if (itemStack == null) {
                    continue;
                }

                this.vendorList.add(new MerchantRecipe(itemStack, ItemStack.EMPTY, new ItemStack(Items.EMERALD, 1), 0, tradesPerDay));
            }
        }
    }

    protected void setupAITasks() {
        this.addTask(30, new EntityAILeaveVillage(this,
                (e) -> !e.isWorkTime(),
                (e) -> e.getVillage().getEdgeNode(),
                EntityVillagerTek.MovementMode.WALK, null,
                () -> {
                    LoggerUtils.instance.info("Killing self...left the village", true);
                    this.setDead();
                }
        ));

        this.addTask(40, new EntityAIWanderVillage(this,
                (e) -> e.isWorkTime(), 3, 60));

        this.addTask(50, new EntityAIVisitVillage(this,
                (e) -> e.isWorkTime() && !this.isTrading(),
                (e) -> e.getVillage().getLastVillagerPos(),
                EntityVillagerTek.MovementMode.WALK, null, null));
    }

    protected void setupServerJobs() {
        super.setupServerJobs();

        this.addJob(new TickJob(100, 0, false,
                () -> this.prepStuck()));

        this.addJob(new TickJob(400, 0, false,
                () -> this.checkStuck()));

        this.addJob(new TickJob(300, 100, true,
                () -> {
                    if (!this.hasVillage() || !this.getVillage().isValid()) {
                        LoggerUtils.instance.info("Killing self...no village", true);
                        this.setDead();
                    }
                }
        ));
    }

    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
    }

    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
    }

    static {
        setupCraftStudioAnimations(ModDetails.MOD_ID, ANIMATION_MODEL_NAME);
    }

}
