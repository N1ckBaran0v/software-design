package traintickets.console.model

data class User(
    val id: UserId? = null,
    val username: String,
    val password: String,
    val name: String,
    val role: String,
    val active: Boolean = true,
)