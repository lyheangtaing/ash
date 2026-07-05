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
    data class SelectAsset(val assetId: String?) : AssetIntent
    data object StartAddAsset : AssetIntent
    data class StartEditAsset(val assetId: String) : AssetIntent
    data class AssetFormChanged(val form: AssetFormState) : AssetIntent
    data object SaveAsset : AssetIntent
    data class DeleteAsset(val assetId: String) : AssetIntent
    data class StartMaintenance(val assetId: String) : AssetIntent
    data class MaintenanceFormChanged(val form: MaintenanceFormState) : AssetIntent
    data object AddMaintenanceRecord : AssetIntent
}
