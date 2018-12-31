package requests

data class GuessRequest(val gameId: Int = -1, val guess: String = "")