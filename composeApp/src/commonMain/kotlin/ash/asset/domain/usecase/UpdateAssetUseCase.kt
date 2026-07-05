package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.repository.AssetRepository

class UpdateAssetUseCase(private val repository: AssetRepository) {
    operator fun invoke(asset: Asset): Asset = repository.upsertAsset(asset)
}
