package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.AssetDecision
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.domain.model.OwnershipStatus
import ash.core.util.DateProvider
import kotlinx.datetime.daysUntil

class CalculateAssetDecisionUseCase {
    operator fun invoke(asset: Asset, estimatedRepairCost: Double? = null): AssetDecisionResult {
        val today = DateProvider.today()
        val warrantyDaysLeft = asset.warrantyEndDate?.let { today.daysUntil(it) }
        val warrantyNearExpiry = warrantyDaysLeft != null && warrantyDaysLeft in 0..30
        val lastMaintenanceDays = asset.latestMaintenanceDate?.let { it.daysUntil(today) }
        val daysSincePurchase = asset.purchaseDate?.daysUntil(today)
        val maintenanceOverdue = asset.ownershipStatus == OwnershipStatus.OWNED &&
            ((lastMaintenanceDays != null && lastMaintenanceDays > 180) ||
                (lastMaintenanceDays == null && daysSincePurchase != null && daysSincePurchase > 365))

        val repairCost = estimatedRepairCost
            ?: asset.maintenanceRecords
                .filter { it.type.contains("repair", ignoreCase = true) }
                .maxOfOrNull { it.cost }
            ?: 0.0

        val highRepairCost = repairCost > 0.0 &&
            (repairCost >= asset.currentEstimatedValue * 0.5 || repairCost >= asset.purchasePrice * 0.35)

        val decision = when {
            asset.condition == AssetCondition.BROKEN && highRepairCost -> AssetDecision.REPLACE
            asset.condition == AssetCondition.BROKEN || asset.condition == AssetCondition.POOR -> AssetDecision.REPAIR
            !asset.isInUse && asset.currentEstimatedValue >= maxOf(50.0, asset.purchasePrice * 0.25) -> AssetDecision.SELL
            asset.currentEstimatedValue <= asset.purchasePrice * 0.15 &&
                asset.condition in setOf(AssetCondition.NEW, AssetCondition.EXCELLENT, AssetCondition.GOOD) -> AssetDecision.KEEP
            asset.condition == AssetCondition.FAIR && !asset.isInUse -> AssetDecision.SELL
            else -> AssetDecision.KEEP
        }

        val reasons = buildList {
            when (decision) {
                AssetDecision.REPLACE -> add("Repair cost is high for a broken asset.")
                AssetDecision.REPAIR -> add("Condition suggests repair before continued use.")
                AssetDecision.SELL -> add("Asset is not in regular use and still has meaningful value.")
                AssetDecision.KEEP -> add("Condition and value support keeping it.")
            }
            if (warrantyNearExpiry) add("Warranty expires within 30 days.")
            if (maintenanceOverdue) add("Maintenance is overdue.")
        }

        return AssetDecisionResult(
            decision = decision,
            warrantyNearExpiry = warrantyNearExpiry,
            maintenanceOverdue = maintenanceOverdue,
            reasons = reasons
        )
    }
}
