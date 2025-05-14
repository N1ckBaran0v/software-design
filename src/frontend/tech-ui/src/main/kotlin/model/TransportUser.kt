package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class TransportUser(
    val id: UserId,
    var username: String,
    var password: String,
    var name: String,
)
