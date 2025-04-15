package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class TransportUser(
    val id: UserId,
    val username: String,
    val password: String,
    val name: String,
)
