package ash.core.navigation

sealed interface AshRoute {
    data object Dashboard : AshRoute
    data object AssetList : AshRoute
    data class AssetDetail(val assetId: String) : AshRoute
    data class EditAsset(val assetId: String?) : AshRoute
    data class MaintenanceLog(val assetId: String) : AshRoute
}
