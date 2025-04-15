package traintickets.console.model

data class RegisterForm(
    val username: String,
    val password: String,
    val confirmPassword: String,
    val name: String,
)
