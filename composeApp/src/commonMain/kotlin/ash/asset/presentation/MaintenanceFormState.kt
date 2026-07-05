package ash.asset.presentation

import ash.asset.domain.model.MaintenanceRecord
import kotlinx.datetime.LocalDate

data class MaintenanceFormState(
    val date: String = "",
    val type: String = "",
    val cost: String = "",
    val notes: String = "",
    val error: String? = null
)

data class MaintenanceFormResult(
    val record: MaintenanceRecord?,
    val error: String?
)

fun MaintenanceFormState.toMaintenanceRecord(id: String): MaintenanceFormResult {
    val parsedDate = try {
        LocalDate.parse(date.trim())
    } catch (_: IllegalArgumentException) {
        null
    } ?: return MaintenanceFormResult(null, "Date must use YYYY-MM-DD.")

    val parsedCost = cost.toDoubleOrNull()
        ?: return MaintenanceFormResult(null, "Cost must be a number.")

    if (type.isBlank()) return MaintenanceFormResult(null, "Type is required.")
    if (parsedCost < 0.0) return MaintenanceFormResult(null, "Cost cannot be negative.")

    return MaintenanceFormResult(
        record = MaintenanceRecord(
            id = id,
            date = parsedDate,
            type = type.trim(),
            cost = parsedCost,
            notes = notes.trim()
        ),
        error = null
    )
}
