package ash.asset.presentation.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class AssetImagePickerController(
    val isAvailable: Boolean,
    private val launchPicker: () -> Unit
) {
    fun launch() = launchPicker()
}

@Composable
expect fun rememberAssetImagePicker(
    onImagesSelected: (List<String>) -> Unit
): AssetImagePickerController

@Composable
expect fun AssetImagePreview(
    uri: String,
    contentDescription: String?,
    modifier: Modifier
)
