package ash.asset.presentation.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode

@Composable
actual fun rememberAssetImagePicker(
    onImagesSelected: (List<String>) -> Unit
): AssetImagePickerController {
    return remember {
        AssetImagePickerController(isAvailable = false) {}
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AssetImagePreview(
    uri: String,
    contentDescription: String?,
    modifier: Modifier
) {
    val image = remember(uri) { loadUIImage(uri) }

    if (image == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {}
    } else {
        UIKitView(
            modifier = modifier.fillMaxSize(),
            factory = {
                UIImageView(image = image).apply {
                    contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                    clipsToBounds = true
                }
            },
            update = { imageView ->
                imageView.image = image
            }
        )
    }
}

private fun loadUIImage(uriText: String): UIImage? {
    val path = uriText
        .trim()
        .removePrefix("file://")
        .takeIf { it.isNotEmpty() }
        ?: return null

    return UIImage.imageWithContentsOfFile(path)
}
