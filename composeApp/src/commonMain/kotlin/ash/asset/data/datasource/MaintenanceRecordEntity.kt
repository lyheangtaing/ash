package ash.asset.data.datasource

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceRecordEntity(
    val id: String,
    val date: LocalDate,
    val type: String,
    val cost: Double,
    val notes: String
)
