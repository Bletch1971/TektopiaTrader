package bletch.tektopiatrader.schedulers;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.tangotek.tektopia.tickjob.TickJob;
import net.tangotek.tektopia.tickjob.TickJobQueue;

public class ScheduleManager {

	protected final World world;
	protected Set<IScheduler> schedulers;
	protected TickJobQueue jobs;

	public ScheduleManager(World worldIn) {
		this.world = worldIn;
		this.schedulers = new HashSet<IScheduler>();
		this.jobs = new TickJobQueue();

		setupServerJobs();
	}

	protected void setupServerJobs() {
		this.addJob(new TickJob(100, 100, true, () -> {
			this.processSchedulers();
		}));
	}

	public void addJob(TickJob job) {
		this.jobs.addJob(job);
	}

	protected void processSchedulers() {
//		VillageManager villageManager = VillageManager.get(this.world);
//		if (villageManager == null)
//			return;
	}

	public void onWorldTick(WorldTickEvent e) {
		this.jobs.tick();
	}
}
