package ash.core.navigation

sealed interface AshRoute {
    data object Collection : AshRoute
    data object Activity : AshRoute
    data object Reminders : AshRoute
    data object Settings : AshRoute
    data class AssetDetail(val assetId: String) : AshRoute
    data class EditAsset(val assetId: String?) : AshRoute
    data class MaintenanceLog(val assetId: String) : AshRoute
    data class EditReminder(val assetId: String?) : AshRoute
}
