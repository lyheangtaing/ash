package ash.asset.presentation.image

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
actual fun rememberAssetImagePicker(
    onImagesSelected: (List<String>) -> Unit
): AssetImagePickerController {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentOnImagesSelected by rememberUpdatedState(onImagesSelected)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
        }
        coroutineScope.launch {
            val storedImages = withContext(Dispatchers.IO) {
                uris.mapNotNull { uri ->
                    copyImageIntoAppStorage(context, uri)
                        ?: uri.toString().takeIf { canOpenImage(context, it) }
                }
            }
            currentOnImagesSelected(storedImages)
        }
    }

    return remember(launcher, coroutineScope) {
        AssetImagePickerController(isAvailable = true) {
            launcher.launch(arrayOf("image/*"))
        }
    }
}

@Composable
actual fun AssetImagePreview(
    uri: String,
    contentDescription: String?,
    modifier: Modifier
) {
    val context = LocalContext.current
    val state by produceState<ImagePreviewState>(initialValue = ImagePreviewState.Loading, uri) {
        value = withContext(Dispatchers.IO) {
            loadImageBitmap(context, uri)
                ?.let(ImagePreviewState::Ready)
                ?: ImagePreviewState.Failed
        }
    }

    when (val currentState = state) {
        ImagePreviewState.Loading -> ImagePlaceholder(modifier, isLoading = true)
        ImagePreviewState.Failed -> ImagePlaceholder(modifier, isLoading = false)
        is ImagePreviewState.Ready -> Image(
            bitmap = currentState.bitmap,
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

private fun loadImageBitmap(context: Context, uriText: String): ImageBitmap? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        decodeWithImageDecoder(context, uriText)?.let { return it }
    }
    return decodeWithBitmapFactory(context, uriText)
}

private fun decodeWithBitmapFactory(context: Context, uriText: String): ImageBitmap? = runCatching {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    openImageStream(context, uriText)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    var sampleSize = 1
    while (bounds.outWidth / sampleSize > MaxPreviewPixels ||
        bounds.outHeight / sampleSize > MaxPreviewPixels
    ) {
        sampleSize *= 2
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    openImageStream(context, uriText)?.use {
        BitmapFactory.decodeStream(it, null, options)?.apply { prepareToDraw() }?.asImageBitmap()
    }
}.getOrNull()

private fun decodeWithImageDecoder(context: Context, uriText: String): ImageBitmap? = runCatching {
    val source = localImageFile(context, uriText)?.let(ImageDecoder::createSource)
        ?: ImageDecoder.createSource(context.contentResolver, Uri.parse(uriText))
    ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
        var sampleSize = 1
        val largestSide = maxOf(info.size.width, info.size.height)
        while (largestSide / sampleSize > MaxPreviewPixels) sampleSize *= 2
        decoder.setTargetSampleSize(sampleSize)
        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
    }.apply { prepareToDraw() }.asImageBitmap()
}.getOrNull()

private fun openImageStream(context: Context, uriText: String) = Uri.parse(uriText).let { uri ->
    when (uri.scheme) {
        AssetImageUriScheme -> localImageFile(context, uriText)?.inputStream()
        null -> File(uriText).takeIf(File::isFile)?.inputStream()
        "file" -> uri.path?.let { File(it).takeIf(File::isFile)?.inputStream() }
        else -> context.contentResolver.openInputStream(uri)
    }
}

private fun copyImageIntoAppStorage(context: Context, uri: Uri): String? {
    var destination: File? = null
    return runCatching {
        val directory = File(context.filesDir, ImageDirectoryName).apply {
            check(isDirectory || mkdirs()) { "Could not create image directory." }
        }
        val extension = context.contentResolver.getType(uri)
            ?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
            ?.takeIf(String::isNotBlank)
            ?: uri.lastPathSegment
                ?.substringAfterLast('.', missingDelimiterValue = "")
                ?.takeIf { it.matches(Regex("[A-Za-z0-9]{1,8}")) }
        val fileName = buildString {
            append(UUID.randomUUID())
            if (!extension.isNullOrBlank()) append('.').append(extension.lowercase())
        }
        val outputFile = File(directory, fileName)
        destination = outputFile
        val bytesCopied = context.contentResolver.openInputStream(uri)?.use { input ->
            outputFile.outputStream().use { output -> input.copyTo(output) }
        } ?: 0L
        check(bytesCopied > 0L) { "Selected image was empty." }
        localAssetImageUri(fileName)
    }.onFailure {
        destination?.delete()
    }.getOrNull()
}

private fun localImageFile(context: Context, uriText: String): File? {
    val localName = uriText.localAssetImageNameOrNull()
    if (localName != null) {
        return File(File(context.filesDir, ImageDirectoryName), localName).takeIf(File::isFile)
    }
    return when (Uri.parse(uriText).scheme) {
        null -> File(uriText).takeIf(File::isFile)
        "file" -> Uri.parse(uriText).path?.let(::File)?.takeIf(File::isFile)
        else -> null
    }
}

private fun canOpenImage(context: Context, uriText: String): Boolean = runCatching {
    openImageStream(context, uriText)?.use { it.read() >= 0 } == true
}.getOrDefault(false)

@Composable
private fun ImagePlaceholder(modifier: Modifier, isLoading: Boolean) {
    Box(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                Icons.Default.BrokenImage,
                contentDescription = "Image unavailable",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private sealed interface ImagePreviewState {
    data object Loading : ImagePreviewState
    data object Failed : ImagePreviewState
    data class Ready(val bitmap: ImageBitmap) : ImagePreviewState
}

private const val MaxPreviewPixels = 1600
private const val ImageDirectoryName = "AssetImages"
