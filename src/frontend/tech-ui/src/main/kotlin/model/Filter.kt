package traintickets.console.model

import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.Date

@Serializable
data class Filter(
    var name: String? = "default",
    var departure: String? = null,
    var destination: String? = null,
    var transfers: Int = 0,
    var passengers: MutableMap<String, Int> = mutableMapOf(),
    var start: String = Date().toString(),
    var end: String = { ->
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        calendar.time.toString()
    } (),
)