package ash.asset.presentation

import ash.asset.domain.usecase.AssetUseCases
import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetDecisionResult
import ash.asset.domain.model.OwnershipStatus
import ash.core.util.DateProvider
import ash.core.util.IdGenerator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AssetViewModel(
    private val useCases: AssetUseCases
) {
    var state by mutableStateOf(AssetUiState())
        private set

    init {
        dispatch(AssetIntent.Load)
    }

    fun decisionFor(asset: Asset): AssetDecisionResult {
        return useCases.calculateAssetDecision(asset)
    }

    fun dispatch(intent: AssetIntent) {
        when (intent) {
            AssetIntent.Load -> refresh()
            AssetIntent.ClearFilters -> {
                state = state.copy(
                    searchQuery = "",
                    categoryFilter = null,
                    conditionFilter = null,
                    ownershipFilter = OwnershipStatus.OWNED
                )
                refresh()
            }
            AssetIntent.ClearEffect -> state = state.copy(effect = null)
            is AssetIntent.SearchChanged -> {
                state = state.copy(searchQuery = intent.query)
                refresh()
            }
            is AssetIntent.CategoryFilterChanged -> {
                state = state.copy(categoryFilter = intent.category)
                refresh()
            }
            is AssetIntent.ConditionFilterChanged -> {
                state = state.copy(conditionFilter = intent.condition)
                refresh()
            }
            is AssetIntent.OwnershipFilterChanged -> {
                state = state.copy(ownershipFilter = intent.ownershipStatus)
                refresh()
            }
            is AssetIntent.SelectAsset -> state = state.copy(selectedAssetId = intent.assetId)
            AssetIntent.StartAddAsset -> state = state.copy(
                editingAssetId = null,
                editForm = AssetFormState(
                    purchaseDate = DateProvider.today().toString(),
                    warrantyEndDate = ""
                )
            )
            is AssetIntent.StartEditAsset -> {
                val asset = useCases.getAssetById(intent.assetId) ?: return
                state = state.copy(
                    selectedAssetId = asset.id,
                    editingAssetId = asset.id,
                    editForm = AssetFormState.fromAsset(asset)
                )
            }
            is AssetIntent.AssetFormChanged -> state = state.copy(editForm = intent.form.copy(error = null))
            AssetIntent.SaveAsset -> saveAsset()
            is AssetIntent.DeleteAsset -> deleteAsset(intent.assetId)
            is AssetIntent.StartMaintenance -> state = state.copy(
                selectedAssetId = intent.assetId,
                maintenanceAssetId = intent.assetId,
                maintenanceForm = MaintenanceFormState(date = DateProvider.today().toString())
            )
            is AssetIntent.MaintenanceFormChanged -> {
                state = state.copy(maintenanceForm = intent.form.copy(error = null))
            }
            AssetIntent.AddMaintenanceRecord -> addMaintenanceRecord()
        }
    }

    private fun saveAsset() {
        val existing = state.editingAsset
        val result = state.editForm.toAsset(existing)
        val asset = result.asset
        if (asset == null) {
            state = state.copy(editForm = state.editForm.copy(error = result.error))
            return
        }

        if (existing == null) {
            useCases.addAsset(asset)
        } else {
            useCases.updateAsset(asset)
        }
        state = state.copy(
            selectedAssetId = asset.id,
            editingAssetId = asset.id,
            effect = AssetEffect.Message("Asset saved.")
        )
        refresh(selectedAssetId = asset.id)
    }

    private fun deleteAsset(assetId: String) {
        useCases.deleteAsset(assetId)
        state = state.copy(
            selectedAssetId = null,
            editingAssetId = null,
            maintenanceAssetId = null,
            effect = AssetEffect.Message("Asset deleted.")
        )
        refresh()
    }

    private fun addMaintenanceRecord() {
        val assetId = state.maintenanceAssetId ?: return
        val result = state.maintenanceForm.toMaintenanceRecord(IdGenerator.next("maintenance"))
        val record = result.record
        if (record == null) {
            state = state.copy(maintenanceForm = state.maintenanceForm.copy(error = result.error))
            return
        }

        useCases.addMaintenanceRecord(assetId, record)
        state = state.copy(
            selectedAssetId = assetId,
            maintenanceForm = MaintenanceFormState(date = DateProvider.today().toString()),
            effect = AssetEffect.Message("Maintenance added.")
        )
        refresh(selectedAssetId = assetId)
    }

    private fun refresh(selectedAssetId: String? = state.selectedAssetId) {
        val assets = useCases.getAssets().sortedWith(
            compareByDescending<ash.asset.domain.model.Asset> { it.updatedAt }
                .thenBy { it.name.lowercase() }
        )
        val searched = useCases.searchAssets(assets, state.searchQuery)
        val visibleAssets = useCases.filterAssets(
            assets = searched,
            category = state.categoryFilter,
            condition = state.conditionFilter,
            ownershipStatus = state.ownershipFilter
        )
        val summary = useCases.calculateAssetSummary(assets)
        state = AssetReducer.reduce(
            state = state.copy(selectedAssetId = selectedAssetId),
            allAssets = assets,
            visibleAssets = visibleAssets,
            summary = summary
        )
    }
}
