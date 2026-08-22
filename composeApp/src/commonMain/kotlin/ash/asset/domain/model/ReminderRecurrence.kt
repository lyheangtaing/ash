package ash.asset.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReminderRecurrence(val label: String) {
    NONE("Does not repeat"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}
