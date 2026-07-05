package ash.asset.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceRecord(
    val id: String,
    val date: LocalDate,
    val type: String,
    val cost: Double,
    val notes: String
)
