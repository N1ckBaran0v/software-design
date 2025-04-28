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
import traintickets.console.utils.Client
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Component
class AuthView(
    private val client: Client,
    private val userView: UserView,
    private val carrierView: CarrierView,
    private val adminView: AdminView,
) {
    @OptIn(ExperimentalEncodingApi::class)
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
                    val tokenForm = Json.decodeFromString<TokenForm>(response.body!!.string())
                    val role = Json.decodeFromString<TokenInfo>(
                        Base64.decode(tokenForm.token.split(".")[1]).decodeToString()
                    ).role
                    actions[role]?.let { it(tokenForm.token) }
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возврат на стартовую страницу.")
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
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
                    val tokenForm = Json.decodeFromString<TokenForm>(response.body!!.string())
                    userView.indexHtml(tokenForm.token)
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возврат на стартовую страницу.")
        }
    }
}