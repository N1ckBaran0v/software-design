package traintickets.console.model

data class Race(
    val id: RaceId? = null,
    val trainId: TrainId,
    val schedule: List<Schedule>,
    val finished: Boolean = false,
)