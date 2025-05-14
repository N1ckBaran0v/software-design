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
    val raceView: RaceView,
    val ticketView: TicketView,
    val filterView: FilterView,
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
        filterView::filter,
        selfView::readSelf,
        ticketView::readTickets,
        raceView::createRace,
        raceView::finishRace,
        trainView::createTrain,
        railcarView::createRailcar,
        userView::readUsers,
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
        loop = false
    }
}