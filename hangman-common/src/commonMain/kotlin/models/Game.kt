package models

data class Game(
    val id: Int = -1,
    val wordId: Int = -1,
    val guessesAllowed: Int = 0
)