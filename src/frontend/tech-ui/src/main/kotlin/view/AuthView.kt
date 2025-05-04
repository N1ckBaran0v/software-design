package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
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
    private val userView: UserView,
    private val carrierView: CarrierView,
    private val adminView: AdminView,
) {
    fun login() {
        try {
            val actions = mapOf(
                "user_role"    to userView::indexHtml,
                "carrier_role" to carrierView::indexHtml,
                "admin_role"   to adminView::indexHtml,
            )
            print("Введите логин: ")
            val login = readLine()!!
            print("Введите пароль: ")
            val password = readLine()!!
            val loginForm = LoginForm(login, password)
            val body = Json.encodeToString(loginForm).toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(client.url("auth/login")).post(body).build()
            client.client.newCall(request).execute().use { response ->
                if (response.code != 200) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: \"${response.body?.string() ?: ""}\"")
                } else {
                    val userData = getUserData(response)
                    actions[userData.role]?.let { it(userData) }
                }
            }
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
            client.client.newCall(request).execute().use { response ->
                if (response.code != 200) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: \"${response.body?.string() ?: ""}\"")
                } else {
                    userView.indexHtml(getUserData(response))
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возврат на стартовую страницу.")
        }
    }

    private fun getUserData(response: Response): UserData {
        val tokenForm = Json.decodeFromString<TokenForm>(response.body!!.string())
        val decoded = String(Base64.getDecoder().decode(tokenForm.token.split(".")[1]))
        val info = Json.decodeFromString<TokenInfo>(decoded)
        return UserData(tokenForm.token, info.id, info.role)
    }
}