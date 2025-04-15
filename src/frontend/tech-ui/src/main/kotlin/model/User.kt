package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: UserId? = null,
    val username: String,
    val password: String,
    val name: String,
    val role: String,
    val active: Boolean = true,
)