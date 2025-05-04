package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class Race(
    val id: RaceId? = null,
    val trainId: TrainId,
    val schedule: MutableList<Schedule> = mutableListOf(),
    val finished: Boolean = false,
)