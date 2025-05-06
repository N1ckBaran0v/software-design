package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.Request
import org.springframework.stereotype.Component
import traintickets.console.model.Ticket
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

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
        } catch (ex: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    private fun printTickets(tickets: List<Ticket>) {
        println("Билеты (полей много, поэтому не форматирую)")
        for (ticket in tickets) {
            println(ticket)
        }
    }
}
