package traintickets.console

import okhttp3.OkHttpClient
import okhttp3.Request

class View(val host: String, val port: Int) {
    private val client = OkHttpClient()

    fun indexHtml() {
        val request = Request.Builder().url("${host}:${port}/bebra").build()
        while (true) {
            client.newCall(request).execute().use { response ->
                println("Server ${response.header("server") ?: "no header"}")
                println("headers: ${response.headers}")
                println(response.body!!.string())
            }
            readlnOrNull()
        }
    }
}