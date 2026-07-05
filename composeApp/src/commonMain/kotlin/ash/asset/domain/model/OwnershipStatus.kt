package ash.asset.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class OwnershipStatus(val label: String) {
    OWNED("Owned"),
    SOLD("Sold"),
    LOST("Lost"),
    REPLACED("Replaced")
}
