package traintickets.console.model

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val ticketId: TicketId? = null,
    val owner: UserId,
    val passenger: String,
    val race: RaceId,
    val railcar: Int,
    val place: Place,
    val start: Schedule,
    val end: Schedule,
    val cost: String,
)