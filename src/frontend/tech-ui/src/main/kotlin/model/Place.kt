package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: PlaceId? = null,
    val number: Int,
    val description: String,
    val purpose: String,
    val cost: Double,
)
