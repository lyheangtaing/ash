package ash.asset.presentation

import ash.asset.domain.model.AssetReminder
import ash.asset.domain.model.ReminderRecurrence
import kotlinx.datetime.LocalDate

data class ReminderFormState(
    val assetId: String = "",
    val action: String = "",
    val dueDate: String = "",
    val time: String = "",
    val recurrence: ReminderRecurrence = ReminderRecurrence.NONE,
    val notes: String = "",
    val error: String? = null
)

data class ReminderFormResult(val reminder: AssetReminder?, val error: String?)

fun ReminderFormState.toReminder(id: String): ReminderFormResult {
    if (assetId.isBlank()) return ReminderFormResult(null, "Choose an asset.")
    if (action.isBlank()) return ReminderFormResult(null, "Action is required.")
    val date = try {
        LocalDate.parse(dueDate.trim())
    } catch (_: IllegalArgumentException) {
        null
    } ?: return ReminderFormResult(null, "Date must use YYYY-MM-DD.")
    val cleanTime = time.trim()
    if (cleanTime.isNotEmpty() && !Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$").matches(cleanTime)) {
        return ReminderFormResult(null, "Time must use 24-hour HH:MM.")
    }
    return ReminderFormResult(
        AssetReminder(
            id = id,
            action = action.trim(),
            dueDate = date,
            time = cleanTime,
            recurrence = recurrence,
            notes = notes.trim()
        ),
        null
    )
}
