package ash.asset.data.datasource

import ash.asset.domain.model.AssetEventType
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class MaintenanceRecordEntity(
    val id: String,
    val date: LocalDate,
    val type: String,
    val cost: Double,
    val notes: String,
    val eventType: AssetEventType = AssetEventType.SERVICE,
    val imageUris: List<String> = emptyList()
)
