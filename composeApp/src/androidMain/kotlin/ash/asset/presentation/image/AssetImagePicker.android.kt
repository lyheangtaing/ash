package ash.asset.presentation.image

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun rememberAssetImagePicker(
    onImagesSelected: (List<String>) -> Unit
): AssetImagePickerController {
    val context = LocalContext.current
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
        currentOnImagesSelected(uris.map(Uri::toString))
    }

    return remember(launcher) {
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
    val bitmap = remember(uri) { loadImageBitmap(context, uri) }

    if (bitmap == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {}
    } else {
        Image(
            bitmap = bitmap,
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

private fun loadImageBitmap(context: Context, uriText: String): ImageBitmap? {
    return runCatching {
        val uri = Uri.parse(uriText)
        val stream = when (uri.scheme) {
            null -> File(uriText).takeIf { it.exists() }?.inputStream()
            "file" -> uri.path?.let { File(it).takeIf(File::exists)?.inputStream() }
            else -> context.contentResolver.openInputStream(uri)
        }
        stream?.use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
    }.getOrNull()
}
