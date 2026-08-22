package ash.asset.domain

import ash.asset.data.datasource.SettingsAssetLocalDataSource
import ash.asset.data.repository.SettingsAssetRepository
import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.AssetReminder
import ash.asset.domain.model.OwnershipStatus
import ash.asset.domain.model.ReminderRecurrence
import ash.asset.domain.usecase.SearchAssetsUseCase
import ash.test.FakeSettings
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ReminderAndSearchTest {
    @Test
    fun completingRecurringReminderCreatesNextOccurrence() {
        val repository = SettingsAssetRepository(SettingsAssetLocalDataSource(FakeSettings()))
        repository.upsertAsset(sampleAsset())
        repository.addReminder(
            "asset-1",
            AssetReminder(
                id = "reminder-1",
                action = "Inspect seals",
                dueDate = LocalDate(2026, 8, 16),
                recurrence = ReminderRecurrence.MONTHLY
            )
        )

        val updated = repository.setReminderCompleted("asset-1", "reminder-1", true, "reminder-2")
        val next = updated?.reminders?.first { it.id == "reminder-2" }

        assertEquals(LocalDate(2026, 9, 16), next?.dueDate)
        assertFalse(next?.isCompleted ?: true)
    }

    @Test
    fun searchFindsTagsAndSpecialDetails() {
        val asset = sampleAsset().copy(tags = listOf("limited"), specialDetails = "Number 14 of 100")
        assertEquals(listOf(asset), SearchAssetsUseCase()(listOf(asset), "limited"))
        assertEquals(listOf(asset), SearchAssetsUseCase()(listOf(asset), "14 of 100"))
    }

    private fun sampleAsset() = Asset(
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
