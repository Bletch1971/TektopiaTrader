package bletch.tektopiatrader.entities.ai;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.entities.ai.EntityAIPatrolPoint;
import net.tangotek.tektopia.structures.VillageStructureMerchantStall;
import net.tangotek.tektopia.structures.VillageStructureType;

public class EntityAIVisitMerchantStall extends EntityAIPatrolPoint {
	protected final EntityVillagerTek entity;
	protected VillageStructureMerchantStall merchantStall;

	public EntityAIVisitMerchantStall(EntityVillagerTek entity, Predicate<EntityVillagerTek> shouldPred, int distanceFromPoint, int waitTime) {
		super(entity, shouldPred, distanceFromPoint, waitTime);
		this.entity = entity;
	}

	protected BlockPos getPatrolPoint() {
		List<?> merchantStalls = this.villager.getVillage().getStructures(VillageStructureType.MERCHANT_STALL);
		if (!merchantStalls.isEmpty()) {
			Collections.shuffle(merchantStalls);
			this.merchantStall = (VillageStructureMerchantStall)merchantStalls.get(0);
		}

		return this.merchantStall != null ? this.merchantStall.getDoor() : null;
	}

	@Override
	public boolean shouldExecute() {
		return this.villager.isAITick() && this.navigator.hasVillage() && this.shouldPred.test(this.villager) ? super.func_75250_a() : false;
	}
}
