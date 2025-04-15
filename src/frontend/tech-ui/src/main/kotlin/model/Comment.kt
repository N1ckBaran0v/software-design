package traintickets.console.model

data class Comment(
    val id: CommentId?,
    val author: UserId,
    val train: TrainId,
    val score: Int,
    val text: String,
)