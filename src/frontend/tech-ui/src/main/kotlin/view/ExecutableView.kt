package traintickets.console.view

import okhttp3.Request
import traintickets.console.utils.Client

abstract class ExecutableView(open val client: Client) {
    protected fun execute(request: Request) {
        client.client.newCall(request).execute().use { response ->
            println("Код возврата ${response.code}. Тело ответа: ${response.body?.string()}")
        }
    }
}