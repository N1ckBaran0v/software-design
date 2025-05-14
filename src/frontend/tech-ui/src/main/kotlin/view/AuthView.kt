package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.LoginForm
import traintickets.console.model.RegisterForm
import traintickets.console.model.TokenForm
import traintickets.console.model.TokenInfo
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import java.util.Base64

@Component
class AuthView(
    private val client: Client,
    private val authorizedView: AuthorizedView,
) {
    fun login() {
        try {
            print("Введите логин: ")
            val login = readLine()!!
            print("Введите пароль: ")
            val password = readLine()!!
            val loginForm = LoginForm(login, password)
            val body = Json.encodeToString(loginForm).toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(client.url("auth/login")).post(body).build()
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возврат на стартовую страницу.")
        }
    }

    fun register() {
        try {
            print("Введите логин: ")
            val login = readLine()!!
            print("Введите пароль: ")
            val password = readLine()!!
            print("Введите пароль ещё раз: ")
            val repeatPassword = readLine()!!
            print("Введите фИО: ")
            val name = readLine()!!
            val registerForm = RegisterForm(login, password, repeatPassword, name)
            val body = Json.encodeToString(registerForm).toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(client.url("auth/register")).post(body).build()
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun execute(request: Request) {
        client.client.newCall(request).execute().use { response ->
            if (response.code >= 400) {
                println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
            } else {
                val tokenForm = Json.decodeFromString<TokenForm>(response.body!!.string())
                val decoded = String(Base64.getDecoder().decode(tokenForm.token.split(".")[1]))
                val info = Json.decodeFromString<TokenInfo>(decoded)
                authorizedView.indexHtml(UserData(tokenForm.token, info.id, info.role))
            }
        }
    }
}