package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenInfo(val iss: String, val id: String, val role: String, val exp: Long)
