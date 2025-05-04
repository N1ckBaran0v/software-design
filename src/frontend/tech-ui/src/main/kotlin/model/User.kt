package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: UserId? = null,
    var username: String,
    var password: String,
    var name: String,
    var role: String,
    var active: Boolean = true,
)