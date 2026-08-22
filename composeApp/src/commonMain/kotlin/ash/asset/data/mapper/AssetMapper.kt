package ash.asset.data.mapper

import ash.asset.data.datasource.AssetEntity
import ash.asset.data.datasource.MaintenanceRecordEntity
import ash.asset.domain.model.Asset
import ash.asset.domain.model.MaintenanceRecord

fun AssetEntity.toDomain(): Asset {
    return Asset(
        id = id,
        name = name,
        category = category,
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        purchaseDate = purchaseDate,
        purchasePrice = purchasePrice,
        currentEstimatedValue = currentEstimatedValue,
        condition = condition,
        ownershipStatus = ownershipStatus,
        warrantyEndDate = warrantyEndDate,
        imageUris = imageUris,
        notes = notes,
        maintenanceRecords = maintenanceRecords.map { it.toDomain() },
        isInUse = isInUse,
        createdAt = createdAt,
        updatedAt = updatedAt,
        tags = tags,
        specialDetails = specialDetails,
        willingToSell = willingToSell,
        desiredSellingPrice = desiredSellingPrice,
        reminders = reminders
    )
}

fun Asset.toEntity(): AssetEntity {
    return AssetEntity(
        id = id,
        name = name,
        category = category,
        brand = brand,
        model = model,
        serialNumber = serialNumber,
        purchaseDate = purchaseDate,
        purchasePrice = purchasePrice,
        currentEstimatedValue = currentEstimatedValue,
        condition = condition,
        ownershipStatus = ownershipStatus,
        warrantyEndDate = warrantyEndDate,
        imageUris = imageUris,
        notes = notes,
        maintenanceRecords = maintenanceRecords.map { it.toEntity() },
        isInUse = isInUse,
        createdAt = createdAt,
        updatedAt = updatedAt,
        tags = tags,
        specialDetails = specialDetails,
        willingToSell = willingToSell,
        desiredSellingPrice = desiredSellingPrice,
        reminders = reminders
    )
}

private fun MaintenanceRecordEntity.toDomain(): MaintenanceRecord {
    return MaintenanceRecord(
        id = id,
        date = date,
        type = type,
        cost = cost,
        notes = notes,
        eventType = eventType,
        imageUris = imageUris
    )
}

private fun MaintenanceRecord.toEntity(): MaintenanceRecordEntity {
    return MaintenanceRecordEntity(
        id = id,
        date = date,
        type = type,
        cost = cost,
        notes = notes,
        eventType = eventType,
        imageUris = imageUris
    )
}
