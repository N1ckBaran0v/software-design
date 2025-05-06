package traintickets.console.view

import org.springframework.stereotype.Component
import traintickets.console.model.UserData
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class RouteView(override val client: Client, val io: IOUtil): ExecutableView(client) {
    fun readRoutes(userData: UserData?) {
        TODO("implement")
    }
}
