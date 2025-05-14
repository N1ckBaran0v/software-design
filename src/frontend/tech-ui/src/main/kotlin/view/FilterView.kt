package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.Filter
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class FilterView(override val client: Client, val io: IOUtil, val routeView: RouteView): ExecutableView(client) {
    private var filter = Filter()

    fun filter() {
        innerFilter()
    }

    fun filter(userData: UserData) {
        innerFilter(userData)
    }

    private fun innerFilter(userData: UserData? = null) {
        io.printList(listOf("Выбрать фильтр", "Ввести параметры вручную"))
        if (io.readNum(2) == 0) {
            getFilters(userData)
        } else {
            defaultFilter()
        }
        val list = listOf(
            "Изменить имя фильтра",
            "Изменить пункт отправления",
            "Изменить пункт назначения",
            "Изменить количество пересадок",
            "Изменить список пассажиров",
            "Изменить минимальное время отправления",
            "Изменить максимальное время прибытия",
            "Выбрать другой фильтр",
            "Сохранить фильтр",
            "Применить фильтр",
            "Выход",
        )
        var flag = true
        while (flag) {
            printFilter(filter)
            io.printList(list)
            when (io.readNum(list.size)) {
                0 -> filter.name = io.readNotEmpty("Введите имя фильтра: ")
                1 -> filter.departure = io.readNotEmpty("Введите пункт отправления: ")
                2 -> filter.destination = io.readNotEmpty("Введите пункт назначения: ")
                3 -> filter.transfers = io.readNum("Введите количество пересадок: ")
                4 -> filter.passengers = getPassengers()
                5 -> filter.start = io.readNotEmpty("Введите минимальное время отправления: ")
                6 -> filter.end = io.readNotEmpty("Введите максимально время прибытия: ")
                7 -> getFilters(userData, false)
                8 -> saveFilter(userData)
                9 -> routeView.readRoutes(userData, filter)
                else -> flag = false
            }
        }
    }

    private fun defaultFilter() {
        filter = Filter()
        filter.departure = io.readNotEmpty("Введите пункт отправления: ")
        filter.destination = io.readNotEmpty("Введите пункт назначения: ")
        filter.setDates()
    }

    private fun getFilters(userData: UserData?, init: Boolean = true) {
        try {
            val request = build(Request.Builder().url(client.url("filters")).get(), userData)
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                    if (init) {
                        println("Переход к ручному вводу параметров")
                        defaultFilter()
                    }
                } else {
                    val body = response.body!!.string()
                    val filters = Json.decodeFromString<List<Filter>>(body)
                    for (i in filters.indices) {
                        filters[i].setDates()
                        println("Фильтр ${i + 1}:")
                        printFilter(filters[i])
                    }
                    getFilter(userData, init)
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
            if (init) {
                println("Переход к ручному вводу параметров")
                defaultFilter()
            }
        }
    }

    private fun getFilter(userData: UserData?, init: Boolean) {
        try {
            val list = listOf("Выбрать фильтр", "Ввести параметры вручную")
            var flag = true
            while (flag) {
                io.printList(list)
                if (io.readNum(2) == 0) {
                    val filterName = io.readNotEmpty("Введите имя фильтра: ")
                    val request = build(Request.Builder().url(client.url("filters?filterName=$filterName")).get(), userData)
                    client.client.newCall(request).execute().use { response ->
                        if (response.code >= 400) {
                            println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                        } else {
                            filter = Json.decodeFromString<List<Filter>>(response.body!!.string())[0]
                            filter.setDates()
                            flag = false
                        }
                    }
                } else {
                    if (init) {
                        defaultFilter()
                    }
                    flag = false
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun getPassengers(): MutableMap<String, Int> {
        val list = listOf("Добавить пассажира", "Выход")
        val map = mutableMapOf<String, Int>()
        var flag = true
        val key = io.readNotEmpty("Введите тип пассажира: ")
        val value = io.readNum("Введите количество: ")
        map[key] = value
        while (flag) {
            io.printList(list)
            if (io.readNum(2) == 0) {
                val key = io.readNotEmpty("Введите тип пассажира: ")
                val value = io.readNum("Введите количество: ")
                map[key] = value
            } else {
                flag = false
            }
        }
        return map
    }

    private fun saveFilter(userData: UserData?) {
        val start = filter.start
        val end = filter.end
        filter.start = null
        filter.end = null
        try {
            val body = Json.encodeToString(filter).toRequestBody("application/json".toMediaType())
            val request = build(Request.Builder().url(client.url("filters")).post(body), userData)
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
        filter.start = start
        filter.end = end
    }

    private fun printFilter(filter: Filter) {
        println("Имя фильтра: ${filter.name}")
        println("Пункт отправления: ${filter.departure}")
        println("Пункт назначения: ${filter.destination}")
        println("Количество пересадок: ${filter.transfers}")
        println("Список пассажиров: ${filter.passengers}")
        println("Минимальное время отправления: ${filter.start}")
        println("Максимальное время прибытия: ${filter.end}")
    }
}