package ash.core.util

import kotlin.random.Random
import kotlin.time.ExperimentalTime

object IdGenerator {
    @OptIn(ExperimentalTime::class)
    fun next(prefix: String): String {
        val timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val suffix = Random.nextInt(1_000, 9_999)
        return "$prefix-$timestamp-$suffix"
    }
}
