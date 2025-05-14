package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.Request
import org.springframework.stereotype.Component
import traintickets.console.model.Filter
import traintickets.console.model.Route
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class RouteView(override val client: Client, val io: IOUtil, val raceView: RaceView): ExecutableView(client) {
    fun readRoutes(userData: UserData?, filter: Filter) {
        try {
            val sb = StringBuilder()
            sb.append("routes")
            sb.append("?name=${filter.name}&departure=${filter.departure}&destination=${filter.destination}")
            sb.append("&transfers=${filter.transfers}&start=${filter.start}&end=${filter.end}")
            for (entry in filter.passengers) {
                sb.append("&passenger_${entry.key}=${entry.value}")
            }
            val request = build(Request.Builder().url(client.url(sb.toString())).get(), userData)
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                } else {
                    val routes = Json.decodeFromString<List<Route>>(response.body!!.string())
                    if (routes.isNotEmpty()) {
                        executeRoutes(userData, routes)
                    }
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun executeRoutes(userData: UserData?, routes: List<Route>) {
        val list = listOf("Просмотреть маршрут", "Выход")
        var flag = true
        while (flag) {
            printRoutes(routes)
            io.printList(list)
            if (io.readNum(2) == 0) {
                val number = io.readNum(routes.size)
                executeRoute(userData, routes[number])
            } else {
                flag = false
            }
        }
    }

    fun executeRoute(userData: UserData?, route: Route) {
        val list = listOf("Посмотреть рейс", "Выход")
        var flag = true
        while (flag) {
            printRoute(route)
            io.printList(list)
            if (io.readNum(2) == 0) {
                val number = io.readNum(route.races.size)
                raceView.readRace(userData, route.races[number].id, route.starts[number], route.ends[number])
            } else {
                flag = false
            }
        }
    }

    fun printRoutes(routes: List<Route>) {
        println("Возможные маршруты:")
        for (i in routes.indices) {
            println("Маршрут ${i + 1}:")
            printRoute(routes[i])
        }
    }

    fun printRoute(route: Route) {
        for (j in route.races.indices) {
            val start = route.starts[j]
            val end = route.ends[j]
            val race = route.races[j]
            println("Рейс ${race.id}: ${start.name}-${end.name} (${start.departure}-${end.arrival})")
        }
    }
}
