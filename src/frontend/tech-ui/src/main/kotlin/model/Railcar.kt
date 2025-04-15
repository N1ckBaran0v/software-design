package traintickets.console.model

data class Railcar(
    val id: RailcarId? = null,
    val model: String,
    val type: String,
    val places: List<Place>,
)
