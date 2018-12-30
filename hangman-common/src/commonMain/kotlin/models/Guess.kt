package models

data class Guess(
    val id: Int = -1,
    val gameId: Int = -1,
    val guess: String = ""
)