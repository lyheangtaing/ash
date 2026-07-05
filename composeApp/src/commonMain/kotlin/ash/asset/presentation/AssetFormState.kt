package ash.asset.presentation

import ash.asset.domain.model.Asset
import ash.asset.domain.model.AssetCategory
import ash.asset.domain.model.AssetCondition
import ash.asset.domain.model.OwnershipStatus
import ash.core.util.DateProvider
import ash.core.util.IdGenerator
import kotlinx.datetime.LocalDate

data class AssetFormState(
    val id: String? = null,
    val name: String = "",
    val category: AssetCategory = AssetCategory.PHONE,
    val brand: String = "",
    val model: String = "",
    val serialNumber: String = "",
    val purchaseDate: String = "",
    val purchasePrice: String = "",
    val currentEstimatedValue: String = "",
    val condition: AssetCondition = AssetCondition.GOOD,
    val ownershipStatus: OwnershipStatus = OwnershipStatus.OWNED,
    val warrantyEndDate: String = "",
    val imageUris: List<String> = emptyList(),
    val pendingImageUri: String = "",
    val notes: String = "",
    val isInUse: Boolean = true,
    val error: String? = null
) {
    companion object {
        fun fromAsset(asset: Asset): AssetFormState {
            return AssetFormState(
                id = asset.id,
                name = asset.name,
                category = asset.category,
                brand = asset.brand,
                model = asset.model,
                serialNumber = asset.serialNumber.orEmpty(),
                purchaseDate = asset.purchaseDate?.toString().orEmpty(),
                purchasePrice = asset.purchasePrice.cleanNumber(),
                currentEstimatedValue = asset.currentEstimatedValue.cleanNumber(),
                condition = asset.condition,
                ownershipStatus = asset.ownershipStatus,
                warrantyEndDate = asset.warrantyEndDate?.toString().orEmpty(),
                imageUris = asset.imageUris,
                notes = asset.notes,
                isInUse = asset.isInUse
            )
        }
    }
}

data class AssetFormResult(
    val asset: Asset?,
    val error: String?
)

fun AssetFormState.toAsset(existing: Asset?): AssetFormResult {
    val trimmedName = name.trim()
    if (trimmedName.isEmpty()) return AssetFormResult(null, "Name is required.")

    val parsedPurchaseDate = purchaseDate.parseDateOrNull()
        ?: return AssetFormResult(null, "Purchase date must use YYYY-MM-DD.")
    val parsedWarrantyDate = if (warrantyEndDate.isBlank()) {
        null
    } else {
        warrantyEndDate.parseDateOrNull()
            ?: return AssetFormResult(null, "Warranty date must use YYYY-MM-DD.")
    }
    val parsedPurchasePrice = purchasePrice.toDoubleOrNull()
        ?: return AssetFormResult(null, "Purchase price must be a number.")
    val parsedCurrentValue = currentEstimatedValue.toDoubleOrNull()
        ?: return AssetFormResult(null, "Current value must be a number.")
    val cleanedImageUris = imageUris
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .distinct()

    if (parsedPurchasePrice < 0.0 || parsedCurrentValue < 0.0) {
        return AssetFormResult(null, "Values cannot be negative.")
    }

    val today = DateProvider.today()
    return AssetFormResult(
        asset = Asset(
            id = existing?.id ?: id ?: IdGenerator.next("asset"),
            name = trimmedName,
            category = category,
            brand = brand.trim(),
            model = model.trim(),
            serialNumber = serialNumber.trim().takeIf { it.isNotEmpty() },
            purchaseDate = parsedPurchaseDate,
            purchasePrice = parsedPurchasePrice,
            currentEstimatedValue = parsedCurrentValue,
            condition = condition,
            ownershipStatus = ownershipStatus,
            warrantyEndDate = parsedWarrantyDate,
            imageUris = cleanedImageUris,
            notes = notes.trim(),
            maintenanceRecords = existing?.maintenanceRecords.orEmpty(),
            isInUse = isInUse,
            createdAt = existing?.createdAt ?: today,
            updatedAt = today
        ),
        error = null
    )
}

private fun String.parseDateOrNull(): LocalDate? {
    return try {
        LocalDate.parse(trim())
    } catch (_: IllegalArgumentException) {
        null
    }
}

private fun Double.cleanNumber(): String {
    return if (this % 1.0 == 0.0) toLong().toString() else toString()
}
