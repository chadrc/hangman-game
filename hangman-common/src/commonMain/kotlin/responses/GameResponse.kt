package responses

data class GameResponse(
    val gameId: Int,
    val word: String = "",
    val guesses: Array<String> = emptyArray(),
    val result: GameResultResponse? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GameResponse

        if (gameId != other.gameId) return false
        if (word != other.word) return false
        if (!guesses.contentEquals(other.guesses)) return false
        if (result != other.result) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = gameId
        result1 = 31 * result1 + word.hashCode()
        result1 = 31 * result1 + guesses.contentHashCode()
        result1 = 31 * result1 + (result?.hashCode() ?: 0)
        return result1
    }
}