package ash.asset.presentation

import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus

sealed interface AssetIntent {
    data object Load : AssetIntent
    data object ClearFilters : AssetIntent
    data object ClearEffect : AssetIntent
    data class SearchChanged(val query: String) : AssetIntent
    data class CategoryFilterChanged(val category: AssetCategory?) : AssetIntent
    data class ConditionFilterChanged(val condition: AssetCondition?) : AssetIntent
    data class OwnershipFilterChanged(val ownershipStatus: OwnershipStatus?) : AssetIntent
    data class TagFilterChanged(val tag: String?) : AssetIntent
    data class SortChanged(val sortOrder: AssetSortOrder) : AssetIntent
    data class SelectAsset(val assetId: String?) : AssetIntent
    data object StartAddAsset : AssetIntent
    data class StartEditAsset(val assetId: String) : AssetIntent
    data class AssetFormChanged(val form: AssetFormState) : AssetIntent
    data object SaveAsset : AssetIntent
    data class DeleteAsset(val assetId: String) : AssetIntent
    data class StartMaintenance(val assetId: String) : AssetIntent
    data class MaintenanceFormChanged(val form: MaintenanceFormState) : AssetIntent
    data object AddMaintenanceRecord : AssetIntent
    data class StartReminder(val assetId: String? = null) : AssetIntent
    data class ReminderFormChanged(val form: ReminderFormState) : AssetIntent
    data object SaveReminder : AssetIntent
    data class ReminderCompleted(val assetId: String, val reminderId: String, val completed: Boolean) : AssetIntent
    data class RequestSuggestion(val assetId: String) : AssetIntent
    data object ClearSuggestion : AssetIntent
}
