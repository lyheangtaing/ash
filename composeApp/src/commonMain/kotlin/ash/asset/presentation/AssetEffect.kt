package ash.asset.presentation

sealed interface AssetEffect {
    data class Message(val text: String) : AssetEffect
}
