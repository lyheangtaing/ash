package ash.asset.domain.usecase

import ash.asset.domain.repository.AssetRepository

class GetAssetsUseCase(private val repository: AssetRepository) {
    operator fun invoke() = repository.getAssets()
}
