package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.Place
import traintickets.console.model.Race
import traintickets.console.model.Schedule
import traintickets.console.model.Ticket
import traintickets.console.model.UserData
import traintickets.console.model.UserId
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil
import java.math.BigDecimal

@Component
class TicketView(override val client: Client, val io: IOUtil): ExecutableView(client) {
    fun readTickets(userData: UserData) {
        try {
            val request = build(Request.Builder().url(client.url("tickets?userId=${userData.id}")).get(), userData)
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                } else {
                    val tickets = Json.decodeFromString<List<Ticket>>(response.body!!.string())
                    printTickets(tickets)
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun getAvailableTickets(userData: UserData?, race: Race, start: Schedule, end: Schedule) {
        try {
            val url = "places?raceId=${race.id!!.id}&departureId=${start.id!!.id}&destinationId=${end.id!!.id}"
            val request = build(Request.Builder().url(client.url(url)).get(), userData)
            client.client.newCall(request).execute().use { response ->
                if (response.code >= 400) {
                    println("Ошибка. Код возврата ${response.code}. Сообщение: ${response.body?.string()}")
                } else {
                    val places = Json.decodeFromString<List<List<Place>>>(response.body!!.string())
                    printPlaces(places)
                    if (userData != null) {
                        executePlaces(userData, places, race, start, end)
                    }
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun executePlaces(
        userData: UserData,
        places: List<List<Place>>,
        race: Race,
        departure: Schedule,
        destination: Schedule,
    ) {
        val list = listOf("Добавить место", "Купить", "Выход")
        var flag = true
        val reserved = mutableSetOf<Pair<Int, Int>>()
        while (flag) {
            io.printList(list)
            when (io.readNum(list.size)) {
                0 -> addPlace(places, reserved)
                1 -> {
                    val tickets = transformReserved(reserved, places, race, departure, destination, userData.id)
                    createTickets(userData, tickets)
                    flag = false
                }
                else -> flag = false
            }
        }
    }

    private fun addPlace(places: List<List<Place>>, reserved: MutableSet<Pair<Int, Int>>) {
        println("Ввод номера вагона.")
        val railcarNumber = io.readNum(places.size)
        val railcar = places[railcarNumber]
        if (railcar.isEmpty()) {
            println("В вагоне нет свободных мест.")
        } else {
            println("Ввод номера места в списке.")
            val index = io.readNum(railcar.size)
            reserved.add(railcarNumber to index)
        }
    }

    private fun transformReserved(
        reserved: MutableSet<Pair<Int, Int>>,
        places: List<List<Place>>,
        race: Race,
        start: Schedule,
        end: Schedule,
        owner: String
    ): List<Ticket> {
        val tickets = mutableListOf<Ticket>()
        for (pair in reserved) {
            val place = places[pair.first][pair.second]
            val diff = BigDecimal.valueOf(end.multiplier).minus(BigDecimal.valueOf(start.multiplier))
            val cost = BigDecimal.valueOf(place.cost).multiply(diff).toDouble()
            tickets.add(Ticket(null, UserId(owner), place.purpose, race.id!!, pair.first + 1, place, start, end, cost))
        }
        return tickets
    }

    private fun createTickets(userData: UserData, tickets: List<Ticket>) {
        try {
            val body = Json.encodeToString(tickets).toRequestBody("application/json".toMediaType())
            val request = build(Request.Builder().url(client.url("tickets")).post(body), userData)
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun printTickets(tickets: List<Ticket>) {
        println("Билеты (полей много, поэтому не форматирую)")
        for (ticket in tickets) {
            println(ticket)
        }
    }

    private fun printPlaces(places: List<List<Place>>) {
        for (i in places.indices) {
            println("Вагон ${i + 1}:")
            val railcar = places[i]
            for (j in railcar.indices) {
                println("${j + 1}) ${railcar[j]}")
            }
        }
    }
}
