package ash.asset.presentation.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import com.lyheang.ash.presentingViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDomainMask
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageView
import platform.UIKit.UIViewContentMode
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

private const val ImageTypeIdentifier = "public.image"
private const val ImageDirectoryName = "AssetImages"
private var activePickerDelegate: PhotoPickerDelegate? = null

@Composable
actual fun rememberAssetImagePicker(
    onImagesSelected: (List<String>) -> Unit
): AssetImagePickerController {
    val currentCallback = rememberUpdatedState(onImagesSelected)
    return remember {
        AssetImagePickerController(isAvailable = true) {
            launchPhotoPicker { currentCallback.value(it) }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun launchPhotoPicker(onImagesSelected: (List<String>) -> Unit) {
    if (activePickerDelegate != null) return
    val presenter = presentingViewController() ?: return
    val configuration = PHPickerConfiguration().apply {
        selectionLimit = 0
        filter = PHPickerFilter.imagesFilter
    }
    val picker = PHPickerViewController(configuration)
    val delegate = PhotoPickerDelegate(onImagesSelected)
    activePickerDelegate = delegate
    picker.delegate = delegate
    presenter.presentViewController(picker, animated = true, completion = null)
}

@OptIn(ExperimentalForeignApi::class)
private class PhotoPickerDelegate(
    private val onImagesSelected: (List<String>) -> Unit
) : NSObject(), PHPickerViewControllerDelegateProtocol {
    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val results = didFinishPicking.filterIsInstance<PHPickerResult>()
        if (results.isEmpty()) {
            finish(emptyList())
            return
        }

        val selectedUris = MutableList<String?>(results.size) { null }
        var remaining = results.size
        results.forEachIndexed { index, result ->
            result.itemProvider.loadFileRepresentationForTypeIdentifier(ImageTypeIdentifier) { url, _ ->
                val copiedUri = url?.let(::copyImageIntoAppStorage)
                dispatch_async(dispatch_get_main_queue()) {
                    selectedUris[index] = copiedUri
                    remaining -= 1
                    if (remaining == 0) finish(selectedUris.filterNotNull())
                }
            }
        }
    }

    private fun finish(uris: List<String>) {
        onImagesSelected(uris)
        activePickerDelegate = null
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun copyImageIntoAppStorage(sourceUrl: NSURL): String? {
    val imagesDirectory = applicationSupportImagesDirectory() ?: return null
    val fileManager = NSFileManager.defaultManager
    if (!fileManager.fileExistsAtPath(imagesDirectory)) {
        val created = fileManager.createDirectoryAtPath(
            path = imagesDirectory,
            withIntermediateDirectories = true,
            attributes = null,
            error = null
        )
        if (!created) return null
    }

    val extension = sourceUrl.pathExtension
        ?.takeIf { it.isNotBlank() }
        ?.let { ".$it" }
        .orEmpty()
    val fileName = "${NSUUID().UUIDString}$extension"
    val destinationUrl = NSURL.fileURLWithPath("$imagesDirectory/$fileName")
    return if (fileManager.copyItemAtURL(sourceUrl, destinationUrl, error = null)) {
        localAssetImageUri(fileName)
    } else {
        null
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
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.BrokenImage,
                contentDescription = "Image unavailable",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    } else {
        UIKitView(
            modifier = modifier.fillMaxSize(),
            factory = {
                UIImageView(image = image).apply {
                    contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                    clipsToBounds = true
                    opaque = true
                }
            },
            update = { imageView -> imageView.image = image }
        )
    }
}

private fun loadUIImage(uriText: String): UIImage? {
    val path = resolveImagePath(uriText) ?: return null
    return UIImage.imageWithContentsOfFile(path)
}

@OptIn(ExperimentalForeignApi::class)
private fun resolveImagePath(uriText: String): String? {
    val fileManager = NSFileManager.defaultManager
    val storedValue = uriText.trim().takeIf(String::isNotEmpty) ?: return null
    val imagesDirectory = applicationSupportImagesDirectory() ?: return null

    storedValue.localAssetImageNameOrNull()?.let { fileName ->
        val localPath = "$imagesDirectory/$fileName"
        return localPath.takeIf(fileManager::fileExistsAtPath)
    }

    val decodedPath = if (storedValue.startsWith("file://", ignoreCase = true)) {
        NSURL.URLWithString(storedValue)?.path
    } else {
        storedValue
    }
    if (decodedPath != null && fileManager.fileExistsAtPath(decodedPath)) return decodedPath

    val fileName = decodedPath?.substringAfterLast('/')?.takeIf(String::isNotBlank) ?: return null
    val recoveredPath = "$imagesDirectory/$fileName"
    return recoveredPath.takeIf(fileManager::fileExistsAtPath)
}

private fun applicationSupportImagesDirectory(): String? {
    val applicationSupport = NSSearchPathForDirectoriesInDomains(
        NSApplicationSupportDirectory,
        NSUserDomainMask,
        true
    ).firstOrNull() as? String ?: return null
    return "$applicationSupport/$ImageDirectoryName"
}
