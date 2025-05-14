package traintickets.console.view

import org.springframework.stereotype.Component
import traintickets.console.utils.IOUtil
import kotlin.system.exitProcess

@Component
class MainView(private val io: IOUtil, private val authView: AuthView, private val filterView: FilterView) {
    fun indexHtml() {
        val list = listOf(
            "Войти в аккаунт",
            "Зарегистрироваться",
            "Посмотреть билеты",
            "Выход",
        )
        val actions = listOf(
            authView::login,
            authView::register,
            filterView::filter,
            this::exit
        )
        while (true) {
            io.printList(list)
            actions[io.readNum(list.size)]()
        }
    }

    private fun exit() {
        println("Завершение работы.")
        exitProcess(0)
    }
}