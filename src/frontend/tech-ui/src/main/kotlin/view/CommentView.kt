package traintickets.console.view

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component
import traintickets.console.model.Comment
import traintickets.console.model.TrainId
import traintickets.console.model.UserData
import traintickets.console.model.UserId
import traintickets.console.utils.Client
import traintickets.console.utils.IOUtil

@Component
class CommentView(override val client: Client, val io: IOUtil): ExecutableView(client) {
    private var flag = false
    fun readComments(userData: UserData?, trainId: TrainId) {
        try {
            flag = true
            while (flag) {
                val request = build(Request.Builder().url(client.url("trains/${trainId.id}/comments")).get(), userData)
                client.client.newCall(request).execute().use { response ->
                    if (response.code < 300) {
                        val body = response.body!!.string()
                        val comments = Json.decodeFromString<List<Comment>>(body)
                        executeComments(userData, trainId, comments)
                    } else {
                        println("Код возврата ${response.code}. Тело ответа: ${response.body?.string()}")
                    }
                }
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер.")
        }
    }

    fun executeComments(userData: UserData?, trainId: TrainId, comments: List<Comment>) {
        val list = listOf("Добавить комментарий", "Удалить комментарий", "Выход")
        printComments(comments)
        io.printList(list)
        when (io.readNum(list.size)) {
            0 -> createComment(userData, trainId)
            1 -> deleteComment(userData, trainId)
            else -> flag = false
        }
    }

    private fun createComment(userData: UserData?, trainId: TrainId) {
        try {
            if (userData != null) {
                val score = io.readNum("Введите оценку от 1 до 5: ")
                println("Введите комментарий: ")
                val text = readLine() ?: ""
                val comment = Comment(null, UserId(userData.id), trainId, score, text)
                val body = Json.encodeToString(comment).toRequestBody("application/json".toMediaType())
                val request = build(Request.Builder().url(client.url("trains/${trainId.id}/comments")).post(body), userData)
                execute(request)
            } else {
                println("Вы не авторизованы.")
            }
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер (или вы не авторизованы).")
        }
    }

    private fun deleteComment(userData: UserData?, trainId: TrainId) {
        try {
            val id = io.readNotEmpty("Введите id комментария: ")
            val body = Json.encodeToString("").toRequestBody("application/json".toMediaType())
            val request = build(Request.Builder().url(client.url("trains/${trainId.id}/comments/$id")).delete(body), userData)
            execute(request)
        } catch (_: Exception) {
            println("Возникла непредвиденная ошибка. Возможно, вырубился сервер (или вы не авторизованы).")
        }
    }

    private fun printComments(comments: List<Comment>) {
        for (comment in comments) {
            printComment(comment)
        }
    }

    private fun printComment(comment: Comment) {
        println("[${comment.id}] ${comment.score} - ${comment.text}")
    }
}
