package traintickets.console.view

import okhttp3.Request
import traintickets.console.model.UserData
import traintickets.console.utils.Client

abstract class ExecutableView(open val client: Client) {
    protected fun execute(request: Request) {
        client.client.newCall(request).execute().use { response ->
            println("Код возврата ${response.code}. Тело ответа: ${response.body?.string()}")
        }
    }

    protected fun build(builder: Request.Builder, userData: UserData?): Request {
        if (userData != null) {
            builder.addHeader("Authorization", "Bearer ${userData.token}")
        }
        return builder.build()
    }
}