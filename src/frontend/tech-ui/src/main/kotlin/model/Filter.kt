package traintickets.console.model

import kotlinx.serialization.Serializable
import java.util.Calendar
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class Filter(
    var user: UserId? = null,
    var name: String? = "default",
    var departure: String? = null,
    var destination: String? = null,
    var transfers: Int = 0,
    var passengers: MutableMap<String, Int> = mutableMapOf("взрослый" to 1),
    var start: String? = null,
    var end: String? = null,
) {
    fun setDates() {
        val curr = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.now()))
        val calendar = Calendar.getInstance()
        calendar.time = curr
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        start = curr.toString()
        end = Timestamp.from(calendar.toInstant()).toString()
    }
}