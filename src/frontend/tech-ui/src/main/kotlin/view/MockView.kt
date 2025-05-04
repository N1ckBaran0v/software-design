package traintickets.console.view

import org.springframework.stereotype.Component
import traintickets.console.model.UserData

@Component
class MockView {
    fun doNothing() {
        println("Функцинальность не реализована.")
    }

    fun doNothing(userData: UserData) {
        doNothing()
    }
}