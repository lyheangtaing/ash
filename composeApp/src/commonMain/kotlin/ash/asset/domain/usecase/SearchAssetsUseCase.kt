package ash.asset.domain.usecase

import ash.asset.domain.model.Asset

class SearchAssetsUseCase {
    operator fun invoke(assets: List<Asset>, query: String): List<Asset> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return assets

        return assets.filter { asset ->
            asset.name.contains(trimmedQuery, ignoreCase = true) ||
                asset.category.label.contains(trimmedQuery, ignoreCase = true) ||
                asset.brand.contains(trimmedQuery, ignoreCase = true) ||
                asset.model.contains(trimmedQuery, ignoreCase = true) ||
                asset.serialNumber.orEmpty().contains(trimmedQuery, ignoreCase = true) ||
                asset.imageUris.any { it.contains(trimmedQuery, ignoreCase = true) }
        }
    }
}
