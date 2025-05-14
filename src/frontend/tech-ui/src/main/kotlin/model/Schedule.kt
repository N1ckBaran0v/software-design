package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
class Schedule(
    val id: ScheduleId? = null,
    val name: String,
    val arrival: String? = "",
    val departure: String? = "",
    val multiplier: Double,
)