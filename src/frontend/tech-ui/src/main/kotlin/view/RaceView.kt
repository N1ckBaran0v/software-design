package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.Race
import traintickets.console.model.Schedule
import traintickets.console.model.TrainId
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class RaceView(override val client: Client, val io: IOUtil, val trainView: TrainView): ExecutableView(client) {
    private var race = Race(trainId = TrainId(""))

    fun createRace(userData: UserData) {
        try {
            val trains = trainView.readTrains(userData)
            if (trains.isEmpty()) {
                println("Поезда не найденв, создать рейс не получится.")
                return
            }
            val trainId = TrainId(io.readNotEmpty("Введите id поезда: "))
            race = Race(trainId = trainId)
            var loop = true
            val list = listOf("Добавить станцию", "Завершить")
            while (loop) {
                io.printList(list)
                if (io.readNum(2) == 0) {
                    race.schedule.add(createSchedule())
                } else {
                    loop = false
                }
            }
            val body = Json.encodeToString(race).toRequestBody("application/json".toMediaType())
            val request = build(Request.Builder().url(client.url("races")).post(body), userData)
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun readRace(userData: UserData?, raceId: String, departure: String, destination: String) {
        try {
            val request = build(Request.Builder().url(client.url("races/$raceId")).get(), userData)
            client.client.newCall(request).execute().use { response ->
                if (response.code < 300) {
                    race = Json.decodeFromString<Race>(response.body!!.string())
                    executeRace(userData, departure, destination)
                } else {
                    println("Код возврата ${response.code}. Тело ответа: ${response.body?.string()}")
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun finishRace(userData: UserData) {
        try {
            val raceId = io.readNotEmpty("Введите id рейса: ")
            val body = Json.encodeToString("").toRequestBody("application/json".toMediaType())
            val request = build(Request.Builder().url(client.url("races/$raceId")).patch(body), userData)
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun createSchedule(): Schedule {
        print("Введите название станции: ")
        val name = readLine() ?: ""
        print("Введите время прибытия: ")
        val arrival = readLine() ?: ""
        print("Введите время отправления: ")
        val departure = readLine() ?: ""
        val price = io.readDouble("Введите множитель цены: ")
        return Schedule(null, name, arrival, departure, price)
    }

    private fun executeRace(userData: UserData?, departure: String, destination: String) {
        val list = listOf("Посмотреть билеты", "Посмотреть отзывы о поезде", "Выход")
        var flag = true
        while (flag) {
            printRace(departure, destination)
            io.printList(list)
            when (io.readNum(list.size)) {
                0 -> TODO("implement")
                1 -> TODO("implement")
                else -> flag = false
            }
        }
    }

    private fun printRace(departureName: String, destinationName: String) {
        val departure = findStation(departureName)
        val destination = findStation(destinationName)
        println("Рейс ${race.id}")
        println("Отправление с ${departure.name}: ${departure.departure}")
        println("Прибытие на ${destination.name}: ${destination.arrival}")
        println("Рейс выполняет поезд ${race.trainId.id}")
    }

    private fun findStation(stationName: String): Schedule {
        for (station in race.schedule) {
            if (station.name == stationName) {
                return station
            }
        }
        throw IllegalArgumentException("Not found (near impossible).")
    }
}
