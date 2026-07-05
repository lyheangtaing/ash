package ash.asset.data.datasource

import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus
import kotlinx.datetime.LocalDate

object DefaultAssetSeed {
    val assets = listOf(
        AssetEntity(
            id = "asset-phone-001",
            name = "Daily phone",
            category = AssetCategory.PHONE,
            brand = "Apple",
            model = "iPhone 15 Pro",
            serialNumber = null,
            purchaseDate = LocalDate(2024, 2, 12),
            purchasePrice = 999.0,
            currentEstimatedValue = 720.0,
            condition = AssetCondition.EXCELLENT,
            ownershipStatus = OwnershipStatus.OWNED,
            warrantyEndDate = LocalDate(2026, 2, 12),
            notes = "Main phone with original box and charger.",
            maintenanceRecords = listOf(
                MaintenanceRecordEntity(
                    id = "maintenance-phone-001",
                    date = LocalDate(2025, 10, 2),
                    type = "Battery health check",
                    cost = 0.0,
                    notes = "Battery health still strong."
                )
            ),
            isInUse = true,
            createdAt = LocalDate(2024, 2, 12),
            updatedAt = LocalDate(2025, 10, 2)
        ),
        AssetEntity(
            id = "asset-laptop-001",
            name = "Work laptop",
            category = AssetCategory.LAPTOP,
            brand = "Apple",
            model = "MacBook Pro 14",
            serialNumber = null,
            purchaseDate = LocalDate(2023, 8, 3),
            purchasePrice = 1999.0,
            currentEstimatedValue = 1420.0,
            condition = AssetCondition.GOOD,
            ownershipStatus = OwnershipStatus.OWNED,
            warrantyEndDate = null,
            notes = "Used for development and design work.",
            maintenanceRecords = listOf(
                MaintenanceRecordEntity(
                    id = "maintenance-laptop-001",
                    date = LocalDate(2025, 6, 4),
                    type = "Laptop cleaning",
                    cost = 25.0,
                    notes = "Cleaned keyboard, screen, and vents."
                )
            ),
            isInUse = true,
            createdAt = LocalDate(2023, 8, 3),
            updatedAt = LocalDate(2025, 6, 4)
        ),
        AssetEntity(
            id = "asset-motorbike-001",
            name = "City motorbike",
            category = AssetCategory.MOTORBIKE,
            brand = "Honda",
            model = "Click 125i",
            serialNumber = null,
            purchaseDate = LocalDate(2021, 1, 16),
            purchasePrice = 2300.0,
            currentEstimatedValue = 1250.0,
            condition = AssetCondition.FAIR,
            ownershipStatus = OwnershipStatus.OWNED,
            warrantyEndDate = null,
            notes = "Reliable city transport. Check tires soon.",
            maintenanceRecords = listOf(
                MaintenanceRecordEntity(
                    id = "maintenance-bike-001",
                    date = LocalDate(2025, 1, 22),
                    type = "Oil change",
                    cost = 14.0,
                    notes = "Changed oil and checked brake pads."
                )
            ),
            isInUse = true,
            createdAt = LocalDate(2021, 1, 16),
            updatedAt = LocalDate(2025, 1, 22)
        ),
        AssetEntity(
            id = "asset-fragrance-001",
            name = "Signature fragrance",
            category = AssetCategory.FRAGRANCE,
            brand = "Dior",
            model = "Sauvage EDP",
            serialNumber = null,
            purchaseDate = LocalDate(2025, 3, 8),
            purchasePrice = 155.0,
            currentEstimatedValue = 90.0,
            condition = AssetCondition.GOOD,
            ownershipStatus = OwnershipStatus.OWNED,
            warrantyEndDate = null,
            notes = "About half full.",
            maintenanceRecords = emptyList(),
            isInUse = false,
            createdAt = LocalDate(2025, 3, 8),
            updatedAt = LocalDate(2025, 3, 8)
        )
    )
}
