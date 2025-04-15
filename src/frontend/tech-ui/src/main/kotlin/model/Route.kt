package traintickets.console.model

data class Route(
    val races: List<RaceId>,
    val starts: List<Schedule>,
    val ends: List<Schedule>,
)