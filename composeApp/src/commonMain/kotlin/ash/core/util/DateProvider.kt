package ash.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

object DateProvider {
    @OptIn(ExperimentalTime::class)
    fun today(): LocalDate {
        return kotlin.time.Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }
}
