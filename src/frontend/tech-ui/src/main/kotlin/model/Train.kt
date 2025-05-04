package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class Train(
    val id: TrainId? = null,
    val trainClass: String,
    val railcars: MutableList<RailcarId> = mutableListOf(),
)