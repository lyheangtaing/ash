package ash.asset.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AssetFormStateTest {
    @Test
    fun quickCreateNeedsPhotoNameAndPurchaseValue() {
        val withoutPhoto = AssetFormState(name = "Camera", purchasePrice = "1200").toAsset(null)
        assertNull(withoutPhoto.asset)
        assertEquals("Add at least one photo.", withoutPhoto.error)

        val result = AssetFormState(
            name = " Camera ",
            purchasePrice = "1200",
            imageUris = listOf("file:///camera.jpg"),
            tagsInput = "photo, limited, photo"
        ).toAsset(null)

        val asset = assertNotNull(result.asset)
        assertEquals("Camera", asset.name)
        assertEquals(1200.0, asset.purchasePrice)
        assertEquals(1200.0, asset.currentEstimatedValue)
        assertNull(asset.purchaseDate)
        assertEquals(listOf("photo", "limited"), asset.tags)
    }

    @Test
    fun invalidOptionalCurrentValueIsReported() {
        val result = AssetFormState(
            name = "Camera",
            purchasePrice = "1200",
            currentEstimatedValue = "unknown",
            imageUris = listOf("file:///camera.jpg")
        ).toAsset(null)

        assertNull(result.asset)
        assertEquals("Current estimated value must be a number.", result.error)
    }
}
