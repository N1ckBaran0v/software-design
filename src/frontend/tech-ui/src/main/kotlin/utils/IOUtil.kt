package traintickets.console.utils

import org.springframework.stereotype.Component

@Component
class IOUtil {
    fun readNum(max: Int): Int {
        while (true) {
            print("Введите целое число в диапазоне от 1 до $max: ")
            val input = readLine()
            if (input != null) {
                try {
                    val number = input.toInt()
                    if (number >= 1 && number <= max) {
                        return number - 1
                    } else {
                        println("Ошибка. Число не попадает в диапазон от 1 до $max.")
                    }
                } catch (_: NumberFormatException) {
                    println("Ошибка. Введено не целое число.")
                }
            }
        }
    }

    fun printList(actions: List<String>) {
        println(actions.joinToString("\n"))
    }
}