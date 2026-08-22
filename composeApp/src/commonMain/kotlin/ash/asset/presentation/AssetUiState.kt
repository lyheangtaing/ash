package ash.asset.presentation

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.AssetSummary
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.domain.model.OwnershipStatus

data class AssetUiState(
    val assets: List<Asset> = emptyList(),
    val visibleAssets: List<Asset> = emptyList(),
    val summary: AssetSummary = AssetSummary.Empty,
    val searchQuery: String = "",
    val categoryFilter: AssetCategory? = null,
    val conditionFilter: AssetCondition? = null,
    val ownershipFilter: OwnershipStatus? = null,
    val tagFilter: String? = null,
    val sortOrder: AssetSortOrder = AssetSortOrder.RECENTLY_ADDED,
    val selectedAssetId: String? = null,
    val editingAssetId: String? = null,
    val maintenanceAssetId: String? = null,
    val editForm: AssetFormState = AssetFormState(),
    val maintenanceForm: MaintenanceFormState = MaintenanceFormState(),
    val reminderForm: ReminderFormState = ReminderFormState(),
    val suggestionAssetId: String? = null,
    val suggestion: AssetDecisionResult? = null,
    val effect: AssetEffect? = null
) {
    val selectedAsset: Asset?
        get() = selectedAssetId?.let { id -> assets.firstOrNull { it.id == id } }

    val editingAsset: Asset?
        get() = editingAssetId?.let { id -> assets.firstOrNull { it.id == id } }

    val maintenanceAsset: Asset?
        get() = maintenanceAssetId?.let { id -> assets.firstOrNull { it.id == id } }
}
