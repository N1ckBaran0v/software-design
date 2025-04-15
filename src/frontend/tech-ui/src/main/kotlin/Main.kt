package traintickets.console

fun printUsage() {
    println("Usage: java -jar ./build/libs/train-tickets-console.jar <host> <port>")
}

fun main(args: Array<String>) {
    try {
        if (args.size != 2) {
            printUsage()
        } else {
            val view = View(args[0], args[1].toInt())
            view.indexHtml()
        }
    } catch (e: NumberFormatException) {
        printUsage()
    }
}