package responses

data class GameResponse(
    val gameId: Int,
    val word: String = "",
    val guesses: List<String> = listOf(),
    val result: GameResultResponse? = null
)