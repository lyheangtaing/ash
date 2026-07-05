package ash.asset.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AssetDecision(val label: String) {
    KEEP("Keep"),
    SELL("Sell"),
    REPAIR("Repair"),
    REPLACE("Replace")
}
