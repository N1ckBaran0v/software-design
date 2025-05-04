package traintickets.console.view

import okhttp3.Request
import traintickets.console.model.UserData
import traintickets.console.utils.Client

abstract class CrudView(open val client: Client) {
    abstract fun create(userData: UserData)
    abstract fun read(userData: UserData)
    abstract fun update(userData: UserData)
    abstract fun delete(userData: UserData)

    protected fun execute(request: Request) {
        client.client.newCall(request).execute().use { response ->
            println("Код возврата ${response.code}. Тело ответа: ${response.body?.string()}")
        }
    }
}