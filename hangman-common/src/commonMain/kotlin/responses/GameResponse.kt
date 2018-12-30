package responses

import models.Game
import models.GameResult
import models.Guess

data class GameResponse(
    val game: Game,
    val word: String? = null,
    val guesses: List<Guess> = listOf(),
    val result: GameResult? = null
)