package traintickets.console

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import traintickets.console.view.MainView

fun main() {
    AnnotationConfigApplicationContext("traintickets.console").getBean(MainView::class.java).indexHtml()
}