package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.TransportUser
import traintickets.console.model.UserData
import traintickets.console.model.UserId
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class SelfView(override val client: Client, val io: IOUtil): ExecutableView(client) {
    private var self = TransportUser(UserId(""), "", "", "")

    fun readSelf(userData: UserData) {
        try {
            val request = build(Request.Builder().url(client.url("users/${userData.id}")).get(), userData)
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                } else {
                    self = Json.decodeFromString<TransportUser>(response.body!!.string())
                    printSelf()
                    val list = listOf("Изменить данные", "Выход")
                    io.printList(list)
                    if (io.readNum(2) == 0) {
                        updateUser(userData)
                    }
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun updateUser(userData: UserData) {
        try {
            var loop = true
            val list = listOf(
                "Сменить логин",
                "Сменить пароль",
                "Сменить ФИО",
                "Готово",
            )
            while (loop) {
                printSelf()
                io.printList(list)
                when (io.readNum(list.size)) {
                    0 -> {
                        print("Введите логин пользователя: ")
                        self.username = readLine() ?: ""
                    }
                    1 -> {
                        print("Введите пароль пользователя: ")
                        self.password = readLine() ?: ""
                    }
                    2 -> {
                        print("Введите ФИО пользователя: ")
                        self.name = readLine() ?: ""
                    }
                    else -> {
                        loop = false
                    }
                }
            }
            val body = Json.encodeToString(self).toRequestBody("application/json".toMediaType())
            val request = build(Request.Builder().url(client.url("users/${self.id.id}")).put(body), userData)
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun printSelf() {
        println("Логин: ${self.username}")
        println("Пароль: ${self.password}")
        println("ФИО: ${self.name}")
    }
}