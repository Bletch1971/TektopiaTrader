package bletch.tektopiatrader.commands;

import bletch.tektopiatrader.entities.EntityTrader;
import bletch.tektopiatrader.utils.LoggerUtils;
import bletch.tektopiatrader.utils.TextUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;
import net.tangotek.tektopia.VillageManager;

import java.util.List;

public class CommandTraderKill extends CommandTraderBase {

    private static final String COMMAND_NAME = "kill";

    public CommandTraderKill() {
        super(COMMAND_NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            throw new WrongUsageException(TraderCommands.COMMAND_PREFIX + COMMAND_NAME + ".usage");
        }

        EntityPlayer entityPlayer = getCommandSenderAsPlayer(sender);
        World world = entityPlayer != null ? entityPlayer.getEntityWorld() : null;

        VillageManager villageManager = world != null ? VillageManager.get(world) : null;
        Village village = villageManager != null && entityPlayer != null ? villageManager.getVillageAt(entityPlayer.getPosition()) : null;
        if (village == null) {
            notifyCommandListener(sender, this, TraderCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage");
            LoggerUtils.info(TextUtils.translate(TraderCommands.COMMAND_PREFIX + COMMAND_NAME + ".novillage"), true);
            return;
        }

        List<EntityTrader> entityList = world.getEntitiesWithinAABB(EntityTrader.class, village.getAABB().grow(Village.VILLAGE_SIZE));
        if (entityList.size() == 0) {
            notifyCommandListener(sender, this, TraderCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists");
            LoggerUtils.info(TextUtils.translate(TraderCommands.COMMAND_PREFIX + COMMAND_NAME + ".noexists"), true);
            return;
        }

        for (EntityTrader entity : entityList) {
            if (entity.isDead)
                continue;

            entity.setDead();

            String name = (entity.isMale() ? TextFormatting.BLUE : TextFormatting.LIGHT_PURPLE) + entity.getName();

            notifyCommandListener(sender, this, TraderCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", name);
            LoggerUtils.info(TextUtils.translate(TraderCommands.COMMAND_PREFIX + COMMAND_NAME + ".success", name), true);
        }
    }

}
