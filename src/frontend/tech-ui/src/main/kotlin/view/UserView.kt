package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.User
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class UserView(override val client: Client, val io: IOUtil): ExecutableView(client) {
    private var user = User(null, "", "", "", "")

    fun createUser(userData: UserData) {
        try {
            print("Введите логин пользователя: ")
            val username = readLine() ?: ""
            print("Введите пароль пользователя: ")
            val password = readLine() ?: ""
            print("Введите ФИО пользователя: ")
            val name = readLine() ?: ""
            print("Введите роль пользователя (user_role/carrier_role/admin_role): ")
            val role = readLine() ?: ""
            user = User(
                username = username,
                password = password,
                name = name,
                role = role
            )
            val body = Json.encodeToString(user).toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(client.url("users"))
                .post(body)
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun readUser(userData: UserData) {
        try {
            val username = io.readNotEmpty("Введите логин пользователя: ")
            val request = Request.Builder()
                .url(client.url("users?username=${username}"))
                .get()
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                } else {
                    user = Json.decodeFromString<User>(response.body!!.string())
                    printUser()
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

    fun readUsers(userData: UserData) {
        try {
            val raceId = io.readNotEmpty("Введите id рейса: ")
            val request = Request.Builder()
                .url(client.url("users?raceId=${raceId}"))
                .get()
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                } else {
                    println(Json.decodeFromString<Map<String, List<String>>>(response.body!!.string()))
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
                "Сменить роль",
                "Сменить статус",
                "Готово",
            )
            while (loop) {
                printUser()
                io.printList(list)
                when (io.readNum(list.size)) {
                    0 -> {
                        print("Введите логин пользователя: ")
                        user.username = readLine() ?: ""
                    }
                    1 -> {
                        print("Введите пароль пользователя: ")
                        user.password = readLine() ?: ""
                    }
                    2 -> {
                        print("Введите ФИО пользователя: ")
                        user.name = readLine() ?: ""
                    }
                    3 -> {
                        print("Введите роль пользователя (user_role/carrier_role/admin_role): ")
                        user.role = readLine() ?: ""
                    }
                    4 -> {
                        user.active = !user.active
                    }
                    else -> {
                        loop = false
                    }
                }
            }
            val body = Json.encodeToString(user).toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(client.url("users/${user.id!!.id}"))
                .put(body)
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun deleteUser(userData: UserData) {
        try {
            val id = io.readNotEmpty("Введите id пользователя: ")
            val request = Request.Builder()
                .url(client.url("users/$id"))
                .delete()
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun printUser() {
        println("Логин: ${user.username}")
        println("Пароль: ${user.password}")
        println("ФИО: ${user.name}")
        println("Роль: ${user.role}")
        println("Статус: ${if (user.active) "Активен" else "Заблокирован"}")
    }
}