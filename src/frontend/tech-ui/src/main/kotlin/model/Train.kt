package traintickets.console.model

data class Train(
    val id: TrainId? = null,
    val trainClass: String,
    val railcars: List<RailcarId>,
)