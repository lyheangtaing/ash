package ash.asset.presentation.image

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AssetImageUriTest {
    @Test
    fun localUriRoundTripsFileName() {
        val uri = localAssetImageUri("photo-123.heic")

        assertEquals("photo-123.heic", uri.localAssetImageNameOrNull())
    }

    @Test
    fun externalUriIsNotTreatedAsPrivateImage() {
        assertNull("content://photos/123".localAssetImageNameOrNull())
        assertNull("file:///tmp/photo.jpg".localAssetImageNameOrNull())
    }

    @Test
    fun privateUriCannotEscapeImageDirectory() {
        assertEquals("photo.jpg", "ash-image:../../photo.jpg".localAssetImageNameOrNull())
    }
}
