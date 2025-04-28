package traintickets.console.view

import org.springframework.stereotype.Component
import traintickets.console.utils.IOUtil
import kotlin.system.exitProcess

@Component
class MainView(val io: IOUtil, val authView: AuthView, val filterView: FilterView) {
    fun indexHtml() {
        val list = listOf(
            "1. Войти в аккаунт.",
            "2. Зарегистрироваться.",
            "3. Посмотреть билеты.",
            "4. Выход.",
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