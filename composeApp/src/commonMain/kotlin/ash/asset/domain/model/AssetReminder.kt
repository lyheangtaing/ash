package ash.asset.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class AssetReminder(
    val id: String,
    val action: String,
    val dueDate: LocalDate,
    val time: String = "",
    val recurrence: ReminderRecurrence = ReminderRecurrence.NONE,
    val notes: String = "",
    val isCompleted: Boolean = false
)
