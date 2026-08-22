package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetSummary
import ash.asset.domain.model.OwnershipStatus
import ash.core.util.DateProvider

class CalculateAssetSummaryUseCase {
    operator fun invoke(assets: List<Asset>): AssetSummary {
        val activeAssets = assets.filter { it.ownershipStatus == OwnershipStatus.OWNED }
        return AssetSummary(
            totalAssetCount = assets.size,
            totalPurchaseValue = assets.sumOf { it.purchasePrice },
            totalCurrentValue = assets.sumOf { it.currentEstimatedValue },
            valueDelta = assets.sumOf { it.currentEstimatedValue - it.purchasePrice },
            assetsNeedingMaintenance = assets.sumOf { asset ->
                asset.reminders.count { !it.isCompleted && it.dueDate <= DateProvider.today() }
            },
            recentAssets = activeAssets.sortedByDescending { it.createdAt }.take(5),
            topValuableAssets = activeAssets.sortedByDescending { it.currentEstimatedValue }.take(3)
        )
    }
}
