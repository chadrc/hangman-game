import kotlin.browser.localStorage

const val gameIdKey: String = "gameId"

fun getGame() {
    val currentGameId = localStorage.getItem(gameIdKey)?.toIntOrNull()
    if (currentGameId != null) {
        State.gettingGame.value = true

        makeGetGameRequest(currentGameId) {
            State.gettingGame.value = false

            State.gameId.value = it.gameId
            State.guesses.value = it.guesses
            State.gameWon.value = it.result?.won
            State.gameForfeit.value = it.result?.forfeit
            State.word.value = it.word
        }
    }
}

fun startGame() {
    State.gettingGame.value = true

    makeStartGameRequest {
        State.gettingGame.value = false

        localStorage.setItem(gameIdKey, it.gameId.toString())

        State.gameId.value = it.gameId
        State.guesses.value = it.guesses
        State.gameWon.value = it.result?.won
        State.gameForfeit.value = it.result?.forfeit
        State.word.value = it.word
    }
}