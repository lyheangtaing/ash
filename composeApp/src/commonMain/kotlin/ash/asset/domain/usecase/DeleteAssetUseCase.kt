package ash.asset.domain.usecase

import ash.asset.domain.repository.AssetRepository

class DeleteAssetUseCase(private val repository: AssetRepository) {
    operator fun invoke(assetId: String) {
        repository.deleteAsset(assetId)
    }
}
