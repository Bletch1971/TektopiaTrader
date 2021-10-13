package bletch.tektopiatrader.schedulers;

import java.util.List;

import bletch.tektopiatrader.core.ModConfig;
import bletch.tektopiatrader.entities.EntityTrader;
import bletch.tektopiatrader.utils.LoggerUtils;
import bletch.tektopiatrader.utils.TektopiaUtils;
import bletch.tektopiatrader.utils.TextUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.tangotek.tektopia.Village;

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

		// if it is night time, then clear the village checks
		this.checkedVillages = false;
		this.resetNight = true;
	}

	@Override
	public void update(World world) {
		// do not process any further if we have already performed the check, it is raining or it is night
		if (this.checkedVillages || world == null || world.isRaining() || Village.isNightTime(world))
			return;
		
		this.resetNight = false;
		this.checkedVillages = true;

		// get a list of the villages from the VillageManager 
		List<Village> villages = TektopiaUtils.getVillages(world);
		if (villages == null || villages.isEmpty())
			return;

		// cycle through each village
		villages.forEach((v) -> {

			// get the village level (1-5) and test to spawn - bigger villages will reduce the number of spawns of the Trader.
			int villageLevel = ModConfig.trader.checksVillageSize ? TektopiaUtils.getVillageLevel(v) : 1;
			int villageCheck = world.rand.nextInt(villageLevel);
			
			if (villageLevel > 0 && villageCheck == 0) {
				
				LoggerUtils.info(TextUtils.translate("message.trader.villagechecksuccess", new Object[] { villageLevel, villageCheck }), true);
				
				// get a list of the Traders in the village
				List<EntityTrader> entityList = world.getEntitiesWithinAABB(EntityTrader.class, v.getAABB().grow(Village.VILLAGE_SIZE));
				if (entityList.size() == 0) {
					
					BlockPos spawnPosition = v.getEdgeNode();

					// attempt spawn
					if (TektopiaUtils.trySpawnEntity(world, spawnPosition, (World w) -> new EntityTrader(w))) {
						v.sendChatMessage(new TextComponentTranslation("message.trader.spawned", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) }));
						LoggerUtils.info(TextUtils.translate("message.trader.spawned", new Object[] { TektopiaUtils.formatBlockPos(spawnPosition) }), true);
					} else {
						v.sendChatMessage(new TextComponentTranslation("message.trader.noposition", new Object[0]));
						LoggerUtils.info(TextUtils.translate("message.trader.noposition", new Object[0]), true);
					}
					
				} else {
					LoggerUtils.info(TextUtils.translate("message.trader.exists", new Object[0]), true);
				}
				
			} else {
				LoggerUtils.info(TextUtils.translate("message.trader.villagecheckfailed", new Object[] { villageLevel, villageCheck }), true);
			}
		});
	}
}
