package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val races: List<RaceId>,
    val starts: List<Schedule>,
    val ends: List<Schedule>,
)