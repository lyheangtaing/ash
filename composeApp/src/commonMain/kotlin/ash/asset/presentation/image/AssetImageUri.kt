package ash.asset.presentation.image

internal const val AssetImageUriScheme = "ash-image"

internal fun localAssetImageUri(fileName: String): String = "$AssetImageUriScheme:$fileName"

internal fun String.localAssetImageNameOrNull(): String? {
    val value = trim()
    val prefix = "$AssetImageUriScheme:"
    if (!value.startsWith(prefix, ignoreCase = true)) return null

    return value
        .substring(prefix.length)
        .removePrefix("//")
        .substringAfterLast('/')
        .takeIf { it.isNotBlank() && it != "." && it != ".." }
}
