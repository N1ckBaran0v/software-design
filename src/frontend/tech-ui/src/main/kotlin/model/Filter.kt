package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    var name: String = "default",
    var departure: String? = null,
    var destination: String? = null,
    var transfers: Int,
    var passengers: Map<String, Int>,
    var start: String,
    var end: String,
)