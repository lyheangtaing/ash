package ash.asset.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Asset(
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
    val maintenanceRecords: List<MaintenanceRecord>,
    val isInUse: Boolean,
    val createdAt: LocalDate,
    val updatedAt: LocalDate
) {
    val totalMaintenanceCost: Double
        get() = maintenanceRecords.sumOf { it.cost }

    val latestMaintenanceDate: LocalDate?
        get() = maintenanceRecords.maxByOrNull { it.date }?.date
}
