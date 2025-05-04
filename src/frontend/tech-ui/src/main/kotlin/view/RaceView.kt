package traintickets.console.view

import org.springframework.stereotype.Component
import traintickets.console.model.Race
import traintickets.console.model.TrainId
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class RaceView(override val client: Client, val io: IOUtil, val railcarView: RailcarView): ExecutableView(client) {
    private var race = Race(trainId = TrainId(""))
}
