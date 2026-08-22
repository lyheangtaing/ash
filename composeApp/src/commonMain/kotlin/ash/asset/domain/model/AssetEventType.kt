package ash.asset.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class AssetEventType(val label: String) {
    REPAIR("Repair"),
    MODIFICATION("Modification"),
    SERVICE("Service"),
    OTHER("Other")
}
