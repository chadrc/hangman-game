package responses

data class GameResponse(
    val gameId: Int,
    val word: String? = null,
    val guesses: List<String> = listOf(),
    val result: GameResultResponse? = null
)