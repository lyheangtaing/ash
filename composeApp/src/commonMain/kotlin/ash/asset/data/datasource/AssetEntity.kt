package ash.asset.data.datasource

import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus
import ash.asset.domain.model.AssetReminder
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AssetEntity(
    val id: String,
    val name: String,
    val category: AssetCategory,
    val brand: String,
    val model: String,
    val serialNumber: String?,
    val purchaseDate: LocalDate?,
    val purchasePrice: Double,
    val currentEstimatedValue: Double,
    val condition: AssetCondition,
    val ownershipStatus: OwnershipStatus,
    val warrantyEndDate: LocalDate?,
    val imageUris: List<String> = emptyList(),
    val notes: String,
    val maintenanceRecords: List<MaintenanceRecordEntity>,
    val isInUse: Boolean,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
    val tags: List<String> = emptyList(),
    val specialDetails: String = "",
    val willingToSell: Boolean = false,
    val desiredSellingPrice: Double? = null,
    val reminders: List<AssetReminder> = emptyList()
)
