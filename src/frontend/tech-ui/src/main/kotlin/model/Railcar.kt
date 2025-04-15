package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class Railcar(
    val id: RailcarId? = null,
    val model: String,
    val type: String,
    val places: List<Place>,
)
