package models

data class GameInfo(
    val game: Game,
    val guesses: List<Guess> = emptyList(),
    val result: GameResult? = null
)