package models

data class GameResult(
    val id: Int = -1,
    val gameId: Int = -1,
    val won: Boolean? = null,
    val forfeit: Boolean? = null
)