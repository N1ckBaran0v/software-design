package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterForm(
    val username: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
)
