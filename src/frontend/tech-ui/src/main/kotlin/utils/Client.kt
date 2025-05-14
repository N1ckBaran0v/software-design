package traintickets.console.utils

import okhttp3.OkHttpClient
import org.springframework.stereotype.Component

@Component
class Client() {
    private val host = "http://localhost"
    private val port = 8080
    val client = OkHttpClient()

    fun url(path: String): String {
        return "$host:$port/api/v1/$path"
    }
}