import responses.GameResponse
import kotlin.browser.localStorage
import kotlin.js.Promise

const val gameIdKey: String = "gameId"

fun getGame() {
    val currentGameId = localStorage.getItem(gameIdKey)?.toIntOrNull()
    if (currentGameId != null) {
        loadingAction(State.gettingGame, makeGetGameRequest(currentGameId))
    }
}

fun startGame() = loadingAction(State.gettingGame, makeStartGameRequest())

fun makeGuess(guess: String) = loadingAction(State.makingGuess, makeGuessRequest(State.gameId.value, guess))

fun forfeitGame() = loadingAction(State.forfeiting, makeForfeitRequest(State.gameId.value))

private fun loadingAction(loadingSwitch: ObservableProp<Boolean>, requestPromise: Promise<GameResponse>) {
    loadingSwitch.value = true
    requestPromise.then {
        loadingSwitch.value = false
        updateStateWithGameResponse(it)
    }
}

private fun updateStateWithGameResponse(gameResponse: GameResponse) {
    localStorage.setItem(gameIdKey, gameResponse.gameId.toString())

    State.gameId.value = gameResponse.gameId
    State.guesses.value = gameResponse.guesses
    State.gameWon.value = gameResponse.result?.won
    State.gameForfeit.value = gameResponse.result?.forfeit
    State.word.value = gameResponse.word
}