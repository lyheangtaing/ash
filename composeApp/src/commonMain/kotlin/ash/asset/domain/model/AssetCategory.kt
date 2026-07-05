package ash.asset.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AssetCategory(val label: String) {
    PHONE("Phone"),
    LAPTOP("Laptop"),
    MOTORBIKE("Motorbike"),
    SHOES("Shoes"),
    FRAGRANCE("Fragrance"),
    COLLECTIBLE("Collectible"),
    DESK_SETUP("Desk setup"),
    ELECTRONICS("Electronics"),
    OTHER("Other")
}
