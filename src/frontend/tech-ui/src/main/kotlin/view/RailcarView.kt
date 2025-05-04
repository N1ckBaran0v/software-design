package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.Place
import traintickets.console.model.Railcar
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class RailcarView(override val client: Client, val io: IOUtil): ExecutableView(client) {
    private var railcar = Railcar(null, "", "")

    fun createRailcar(userData: UserData) {
        try {
            print("Введите модель вагона: ")
            val model = readLine() ?: ""
            print("Введите тип вагона: ")
            val type = readLine() ?: ""
            railcar = Railcar(
                model = model,
                type = type,
            )
            var loop = true
            val list = listOf("Добавить место", "Завершить")
            while (loop) {
                io.printList(list)
                if (io.readNum(2) == 0) {
                    railcar.places.add(createPlace())
                } else {
                    loop = false
                }
            }
            val body = Json.encodeToString(railcar).toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(client.url("railcars"))
                .post(body)
                .addHeader("Authorization", "Bearer ${userData.token}")
                .build()
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun createPlace(): Place {
        val number = io.readNum("Введите номер места: ")
        print("Введите описание места: ")
        val description = readLine() ?: ""
        print("Введите назначение места: ")
        val purpose = readLine() ?: ""
        val price = io.readDouble("Введите цену на место: ")
        return Place(null, number, description, purpose, price)
    }
}