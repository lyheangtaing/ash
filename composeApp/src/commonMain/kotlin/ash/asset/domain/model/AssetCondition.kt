package ash.asset.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AssetCondition(val label: String) {
    NEW("New"),
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor"),
    BROKEN("Broken")
}
