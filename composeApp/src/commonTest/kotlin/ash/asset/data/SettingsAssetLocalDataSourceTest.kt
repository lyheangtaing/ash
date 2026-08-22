package ash.asset.data

import ash.asset.data.datasource.AssetEntity
import ash.asset.data.datasource.SettingsAssetLocalDataSource
import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus
import ash.test.FakeSettings
import kotlinx.datetime.LocalDate
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsAssetLocalDataSourceTest {
    @Test
    fun olderRecordsDecodeWithNewFieldsAtDefaults() {
        val settings = FakeSettings()
        val oldRecord = sampleEntity()
        settings.putString(
            "ash.assets.v1",
            Json { encodeDefaults = false }.encodeToString(ListSerializer(AssetEntity.serializer()), listOf(oldRecord))
        )

        val restored = SettingsAssetLocalDataSource(settings).getAssets().single()

        assertTrue(restored.tags.isEmpty())
        assertTrue(restored.reminders.isEmpty())
        assertFalse(restored.willingToSell)
        assertEquals("", restored.specialDetails)
    }

    @Test
    fun freshStorageStartsAsAnEmptyPrivateCollection() {
        val dataSource = SettingsAssetLocalDataSource(FakeSettings())
        assertTrue(dataSource.getAssets().isEmpty())
    }

    private fun sampleEntity() = AssetEntity(
        id = "asset-1",
        name = "Camera",
        category = AssetCategory.ELECTRONICS,
        brand = "Leica",
        model = "Q3",
        serialNumber = null,
        purchaseDate = null,
        purchasePrice = 6000.0,
        currentEstimatedValue = 5600.0,
        condition = AssetCondition.EXCELLENT,
        ownershipStatus = OwnershipStatus.OWNED,
        warrantyEndDate = null,
        notes = "",
        maintenanceRecords = emptyList(),
        isInUse = true,
        createdAt = LocalDate(2025, 1, 1),
        updatedAt = LocalDate(2025, 1, 1)
    )
}
