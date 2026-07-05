package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetSummary
import ash.asset.domain.model.OwnershipStatus

class CalculateAssetSummaryUseCase(
    private val calculateAssetDecision: CalculateAssetDecisionUseCase
) {
    operator fun invoke(assets: List<Asset>): AssetSummary {
        val activeAssets = assets.filter { it.ownershipStatus == OwnershipStatus.OWNED }
        return AssetSummary(
            totalAssetCount = activeAssets.size,
            totalPurchaseValue = activeAssets.sumOf { it.purchasePrice },
            totalCurrentValue = activeAssets.sumOf { it.currentEstimatedValue },
            valueDelta = activeAssets.sumOf { it.currentEstimatedValue - it.purchasePrice },
            assetsNeedingMaintenance = activeAssets.count { calculateAssetDecision(it).maintenanceOverdue },
            recentAssets = activeAssets.sortedByDescending { it.createdAt }.take(5),
            topValuableAssets = activeAssets.sortedByDescending { it.currentEstimatedValue }.take(3)
        )
    }
}
