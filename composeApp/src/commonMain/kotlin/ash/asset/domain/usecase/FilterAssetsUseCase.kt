package ash.asset.domain.usecase

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus

class FilterAssetsUseCase {
    operator fun invoke(
        assets: List<Asset>,
        category: AssetCategory?,
        condition: AssetCondition?,
        ownershipStatus: OwnershipStatus?
    ): List<Asset> {
        return assets.filter { asset ->
            (category == null || asset.category == category) &&
                (condition == null || asset.condition == condition) &&
                (ownershipStatus == null || asset.ownershipStatus == ownershipStatus)
        }
    }
}
