package bletch.tektopiatrader.schedulers;

import bletch.common.schedulers.IScheduler;
import bletch.common.utils.TektopiaUtils;
import bletch.common.utils.TextUtils;
import bletch.tektopiatrader.core.ModConfig;
import bletch.tektopiatrader.entities.EntityTrader;
import bletch.tektopiatrader.utils.LoggerUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;

import java.util.List;

public class TraderScheduler implements IScheduler {

    protected Boolean checkedVillages = false;
    protected Boolean resetNight = false;

    @Override
    public void resetDay() {
    }

    @Override
    public void resetNight() {
        if (this.resetNight)
            return;

        LoggerUtils.info("TraderScheduler - resetNight called", true);

        // if it is night time, then clear the village checks
        this.checkedVillages = false;
        this.resetNight = true;
    }

    @Override
    public void update(World world) {
        // do not process any further if we have already performed the check, it is raining or it is night
        if (this.checkedVillages || world == null || world.isRaining() || !EntityTrader.isWorkTime(world, 0))
            return;

        LoggerUtils.info("TraderScheduler - update called", true);

        this.resetNight = false;
        this.checkedVillages = true;

        // get a list of the villages from the VillageManager
        List<Village> villages = TektopiaUtils.getVillages(world);
        if (villages == null || villages.isEmpty())
            return;

        // cycle through each village
        villages.forEach((v) -> {

            String villageName = v.getName();

            // get the village level (1-5) and test to spawn - bigger villages will reduce the number of spawns of the Trader.
            int villageLevel = TektopiaUtils.getVillageLevel(v);
            int villageCheck = ModConfig.trader.checksVillageSize ? world.rand.nextInt(villageLevel) : 0;

            if (villageLevel > 0 && villageCheck == 0) {

                LoggerUtils.info(TextUtils.translate("message.trader.villagechecksuccess", villageName, villageLevel, villageCheck), true);

                // get a list of the Traders in the village
                List<EntityTrader> entityList = world.getEntitiesWithinAABB(EntityTrader.class, v.getAABB().grow(Village.VILLAGE_SIZE));
                if (entityList.size() == 0) {

                    BlockPos spawnPosition = TektopiaUtils.getVillageSpawnPoint(world, v);

                    // attempt spawn
                    if (TektopiaUtils.trySpawnEntity(world, spawnPosition, (World w) -> new EntityTrader(w))) {
                        v.sendChatMessage(new TextComponentTranslation("message.trader.spawned"));
                        LoggerUtils.info(TextUtils.translate("message.trader.spawned.village", villageName, TektopiaUtils.formatBlockPos(spawnPosition)), true);
                    } else {
                        LoggerUtils.info(TextUtils.translate("message.trader.noposition.village", villageName), true);
                    }

                } else {
                    LoggerUtils.info(TextUtils.translate("message.trader.exists", villageName), true);
                }

            } else {
                LoggerUtils.info(TextUtils.translate("message.trader.villagecheckfailed", villageName, villageLevel, villageCheck), true);
            }
        });
    }
}
