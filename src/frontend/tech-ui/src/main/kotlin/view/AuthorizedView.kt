package traintickets.console.view

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class AuthorizedView(
    val client: Client,
    val io: IOUtil,
    val userView: UserView,
    val selfView: SelfView,
    val railcarView: RailcarView,
    val trainView: TrainView,
    val ticketView: TicketView,
    val mockView: MockView,
) {
    private var loop = true
    private val list = mutableListOf(
        "Найти билеты",
        "Посмотреть данные аккаунта",
        "Посмотреть список билетов",
        "Добавить рейс",
        "Завершить рейс",
        "Добавить поезд",
        "Добавить вагон",
        "Получить список пассажиров",
        "Создать пользователя",
        "Удалить пользователя",
        "Получить данные о пользователе",
        "Выход",
    )
    private val actions = mutableListOf(
        mockView::doNothing,
        selfView::readSelf,
        ticketView::readTickets,
        mockView::doNothing,
        mockView::doNothing,
        trainView::createTrain,
        railcarView::createRailcar,
        mockView::doNothing,
        userView::createUser,
        userView::deleteUser,
        userView::readUser,
        this::exit,
    )

    fun indexHtml(userData: UserData) {
        while (loop) {
            io.printList(list)
            val num = io.readNum(list.size)
            actions[num](userData)
        }
        loop = true
    }

    private fun exit(userData: UserData) {
        try {
            val body = "".toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(client.url("auth/logout"))
                .post(body)
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                    println("Хотя какая разница, есть ли ошибка:)")
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
        loop = false
    }
}