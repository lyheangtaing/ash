package ash.asset.domain.model

data class AssetSummary(
    val totalAssetCount: Int,
    val totalPurchaseValue: Double,
    val totalCurrentValue: Double,
    val valueDelta: Double,
    val assetsNeedingMaintenance: Int,
    val recentAssets: List<Asset>,
    val topValuableAssets: List<Asset>
) {
    companion object {
        val Empty = AssetSummary(
            totalAssetCount = 0,
            totalPurchaseValue = 0.0,
            totalCurrentValue = 0.0,
            valueDelta = 0.0,
            assetsNeedingMaintenance = 0,
            recentAssets = emptyList(),
            topValuableAssets = emptyList()
        )
    }
}
