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

fun makeGuess() = loadingAction(
    State.makingGuess,
    makeGuessRequest(
        State.gameId.value,
        State.guessText.value.trim()
    ),
    resetGuessText
)

fun forfeitGame() = loadingAction(State.forfeiting, makeForfeitRequest(State.gameId.value))

val resetGuessText = { updateGuessText("") }

fun updateGuessText(text: String) {
    State.guessText.value = text
}

private fun loadingAction(
    loadingSwitch: ObservableProp<Boolean>,
    requestPromise: Promise<GameResponse>,
    after: () -> Unit = {}
) {
    loadingSwitch.value = true
    requestPromise.then {
        loadingSwitch.value = false
        updateStateWithGameResponse(it)
        after()
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