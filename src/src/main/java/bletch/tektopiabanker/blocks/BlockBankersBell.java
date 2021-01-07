package bletch.tektopiabanker.blocks;

import java.util.List;
import java.util.function.Function;

import bletch.tektopiabanker.core.ModConfig;
import bletch.tektopiabanker.core.ModSounds;
import bletch.tektopiabanker.entities.EntityBanker;
import bletch.tektopiabanker.tileentities.TileEntityBankersBell;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureTownHall;
import net.tangotek.tektopia.structures.VillageStructureType;

public class BlockBankersBell extends BlockContainer {
	
	protected String name = "bankersbell";
	protected long lastActivated = 0;
	
	public BlockBankersBell() {
		super(Material.GROUND);
		
		setRegistryName(getName());
		setUnlocalizedName(getName());
		setSoundType(SoundType.METAL);
		setHarvestLevel("pickaxe", 0);
		setHardness(1F);
		setResistance(10F);
		setLightLevel(0F);
		setLightOpacity(0);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}
	
	public String getName() {
		return this.name;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.4F, 0F, 0.4F, 0.6F, 0.2F, 0.6F);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entity, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		super.onBlockActivated(world, pos, state, entity, hand, side, hitX, hitY, hitZ);
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		// play the bell ding sound
		world.playSound((EntityPlayer)null, x, y, z, ModSounds.BellRing, SoundCategory.BLOCKS, (float)1, (float)1);
		
		if (!world.isRemote) {
			// check if the bell has been rung within the last few ticks
			Long worldTime = world.getTotalWorldTime();
			
			if (worldTime - 5 > this.lastActivated) {
				// try to spawn a banker
				trySpawnBanker(world, pos, (EntityPlayerMP)entity, (World w) -> new EntityBanker(w));
				// set the last activated time
				this.lastActivated = worldTime;
			}
		}
		
		return true;
	} 

	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {

	}
	
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}
	   
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBankersBell();
	} 

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityBankersBell();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	} 

	private void trySpawnBanker(World world, BlockPos pos, EntityPlayerMP entity, Function<World, ?> createFunc) {
		// check if the banker can be spawned
		if (ModConfig.banker.dayTimeOnly && !EntityBanker.isWorkTime(world)) {
			entity.sendMessage(new TextComponentTranslation("message.banker.notworktime"));
			return;
		}
		
		// check if the bell position is within the Town Hall
		VillageManager manager = VillageManager.get(world);
		if (manager == null) {
			return;
		}
		
		Village village = manager.getNearestVillage(pos, 20);
		if (village == null) {
			entity.sendMessage(new TextComponentTranslation("message.banker.notintownhall"));
			return;
		}
		
		VillageStructure structure = village.getNearestStructure(VillageStructureType.TOWNHALL, pos);
		if (!(structure instanceof VillageStructureTownHall)) {
			entity.sendMessage(new TextComponentTranslation("message.banker.notintownhall"));
			return;
		}
		
		VillageStructureTownHall townHallStructure = (VillageStructureTownHall)structure;
		
		// get a list of the Bankers within the Town Hall
		List<EntityBanker> merchantList = world.getEntitiesWithinAABB(EntityBanker.class, townHallStructure.getAABB().grow(2.0D, 3.0D, 2.0D));

		// while there are more than one Banker, remove all but one.
		while(merchantList.size() > 1) {
			((EntityBanker)merchantList.get(0)).setDead();
			merchantList.remove(0);
		}

		// if there are no Bankers, then spawn one
		if (merchantList.isEmpty()) {
			BlockPos randomPosition = townHallStructure.getRandomFloorTile();
			
			if (randomPosition == null) {
				entity.sendMessage(new TextComponentTranslation("message.banker.nosafeplace"));
				
			} else {
				EntityBanker banker = (EntityBanker)createFunc.apply(world);
				banker.setLocationAndAngles((double)randomPosition.getX() + 0.5D, (double)randomPosition.getY(), (double)randomPosition.getZ() + 0.5D, 0.0F, 0.0F);
				banker.onInitialSpawn(world.getDifficultyForLocation(randomPosition), (IEntityLivingData)null);
				world.spawnEntity(banker);
			}
		}
	}

}
