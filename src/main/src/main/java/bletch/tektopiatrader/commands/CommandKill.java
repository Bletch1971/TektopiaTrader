package bletch.tektopiatrader.commands;

import java.util.List;

import bletch.tektopiatrader.core.ModCommands;
import bletch.tektopiatrader.entities.EntityTrader;
import bletch.tektopiatrader.utils.LoggerUtils;
import bletch.tektopiatrader.utils.TextUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;

public class CommandKill extends TraderCommandBase {

	private static final String COMMAND_NAME = "kill";
	
	public CommandKill() {
		super(COMMAND_NAME);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 0) {
			throw new WrongUsageException(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage", new Object[0]);
		} 
		
		EntityPlayer entityPlayer = super.getCommandSenderAsPlayer(sender);
		World world = entityPlayer != null ? entityPlayer.getEntityWorld() : null;
		
		VillageManager villageManager = world != null ? VillageManager.get(world) : null;
		Village village = villageManager != null && entityPlayer != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
		if (village == null) {
			notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage", new Object[0]);
			LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage", new Object[0]), true);
			return;
		}

        List<EntityTrader> entityList = world.getEntitiesWithinAABB(EntityTrader.class, village.getAABB().grow(Village.VILLAGE_SIZE));
        if (entityList.size() == 0) {
			notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists", new Object[0]);
			LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists", new Object[0]), true);
			return;
        }
        
        for (EntityTrader entity : entityList) {
        	if (entity.isDead)
        		continue;
        	
        	entity.setDead();
    		
    		notifyCommandListener(sender, this, ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", new Object[0]);
    		LoggerUtils.info(TextUtils.translate(ModCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", new Object[0]), true);
        }
	}
    
}
