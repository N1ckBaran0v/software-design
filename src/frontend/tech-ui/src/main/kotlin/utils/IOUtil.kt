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
            } else {
                println("Ошибка. Строка пустая.")
            }
        }
    }

    fun readNum(message: String = "Введите целое число: "): Int {
        while (true) {
            print(message)
            val input = readLine()
            if (input != null) {
                try {
                    return input.toInt()
                } catch (_: NumberFormatException) {
                    println("Ошибка. Введено не целое число.")
                }
            } else {
                println("Ошибка. Строка пустая.")
            }
        }
    }

    fun readDouble(message: String = "Введите вещественное число: "): Double {
        while (true) {
            print(message)
            val input = readLine()
            if (input != null) {
                try {
                    return input.toDouble()
                } catch (_: NumberFormatException) {
                    println("Ошибка. Введено не целое число.")
                }
            } else {
                println("Ошибка. Строка пустая.")
            }
        }
    }

    fun readNotEmpty(message: String = "Введите непустую строку: "): String {
        while (true) {
            print(message)
            val input = readLine()
            if (input == null || input.isBlank()) {
                println("Ошибка. Строка пустая.")
            } else {
                return input
            }
        }
    }

    fun printList(actions: List<String>) {
        val sb = StringBuilder()
        var num = 0
        for (action in actions) {
            num++
            sb.append("$num) $action.\n")
        }
        print(sb)
    }
}