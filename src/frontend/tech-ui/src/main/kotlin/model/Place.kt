package traintickets.console.model

import java.math.BigDecimal

data class Place(
    val id: PlaceId? = null,
    val number: Int,
    val description: String,
    val purpose: String,
    val cost: BigDecimal,
)
