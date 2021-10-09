package bletch.tektopiatrader.commands;

import java.util.function.Function;

import bletch.tektopiatrader.core.ModCommands;
import bletch.tektopiatrader.entities.EntityTrader;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;
import net.tangotek.tektopia.structures.VillageStructure;
import net.tangotek.tektopia.structures.VillageStructureTownHall;
import net.tangotek.tektopia.structures.VillageStructureType;

public class CommandSpawn extends CommandVillageBase {

	private static final String COMMAND_NAME = "spawn";
	
	public CommandSpawn() {
		super(COMMAND_NAME);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0) {
			throw new WrongUsageException(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage", new Object[0]);
		} else {
			// attempt to spawn the trader
			EntityPlayer entityPlayer = super.getCommandSenderAsPlayer(sender);
			VillageManager villageManager = entityPlayer != null ? VillageManager.get(entityPlayer.getEntityWorld()) : null;
			Village village = villageManager != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
			
			if (village != null) {
				//BlockPos spawnPosition = village.getEdgeNode();
				VillageStructure structure = village.getNearestStructure(VillageStructureType.TOWNHALL, entityPlayer.getPosition());
				VillageStructureTownHall townHallStructure = structure != null ? (VillageStructureTownHall)structure : null;
				BlockPos spawnPosition = townHallStructure != null ? townHallStructure.getRandomFloorTile() : null;
				if (spawnPosition == null)
					return;
				
				Boolean entitySpawned = trySpawnEntity(entityPlayer.getEntityWorld(), spawnPosition, (World w) -> new EntityTrader(w));
				
				if (entitySpawned) {
					notifyCommandListener(sender, this, "commands.trader.spawn.success", new Object[] { spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ() });
					village.debugOut("Spawning Trader at " + spawnPosition);
				} else {
					notifyCommandListener(sender, this, "commands.trader.spawn.failed", new Object[0]);
					village.debugOut("Spawning Trader failed");
				}
			}
		}
	}

	private static Boolean trySpawnEntity(World world, BlockPos spawnPosition, Function<World, ?> createFunc) {
		if (world == null || spawnPosition == null || createFunc == null)
			return false;
		
		EntityLiving entity = (EntityLiving)createFunc.apply(world);
		if (entity == null)
			return false;
		
		entity.setLocationAndAngles((double)spawnPosition.getX() + 0.5D, (double)spawnPosition.getY(), (double)spawnPosition.getZ() + 0.5D, 0.0F, 0.0F);
		entity.onInitialSpawn(world.getDifficultyForLocation(spawnPosition), (IEntityLivingData)null);
		return world.spawnEntity(entity);
   }
}
