package ash.asset.presentation

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetSummary

object AssetReducer {
    fun reduce(
        state: AssetUiState,
        allAssets: List<Asset>,
        visibleAssets: List<Asset>,
        summary: AssetSummary
    ): AssetUiState {
        val selectedId = state.selectedAssetId?.takeIf { id -> allAssets.any { it.id == id } }
        return state.copy(
            assets = allAssets,
            visibleAssets = visibleAssets,
            summary = summary,
            selectedAssetId = selectedId
        )
    }
}
