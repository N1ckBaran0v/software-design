package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.Railcar
import traintickets.console.model.RailcarId
import traintickets.console.model.Train
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class TrainView(override val client: Client, val io: IOUtil, val railcarView: RailcarView): ExecutableView(client) {
    private var train = Train(null, "", mutableListOf())

    fun createTrain(userData: UserData) {
        try {
            print("Введите класс поезда: ")
            val trainClass = readLine() ?: ""
            train = Train(trainClass = trainClass)
            var loop = true
            val list = listOf("Получить список вагонов", "Добавить вагон", "Завершить")
            while (loop) {
                io.printList(list)
                when (io.readNum(3)) {
                    0 -> printRailcars(railcarView.readRailcars(userData))
                    1 -> train.railcars.add(RailcarId(io.readNotEmpty("Введите id вагона:")))
                    else -> loop = false
                }
            }
            val body = Json.encodeToString(train).toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(client.url("trains"))
                .post(body)
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun readTrains(userData: UserData): List<Train> {
        try {
            val start = io.readNotEmpty("Введите время начала забронированного участка: ")
            val end = io.readNotEmpty("Введите время конца забронированного участка: ")
            val request = Request.Builder()
                .url(client.url("trains?start=$start&end=$end"))
                .get()
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            client.client.newCall(request).execute().use { response ->
                if (response.code < 300) {
                    return Json.decodeFromString<List<Train>>(response.body!!.string())
                } else {
                    println("Код возврата ${response.code}. Тело ответа: ${response.body?.string()}")
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
        return emptyList()
    }

    private fun printRailcars(railcars: List<Railcar>) {
        if (!railcars.isEmpty()) {
            println("Вагоны:")
        }
        for (railcar in railcars) {
            println(railcar)
        }
    }
}
