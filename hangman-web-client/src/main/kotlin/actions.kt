import responses.GameResponse

fun startGame() {
    State.startingGameProp.value = true
    makeRequest<GameResponse>("/start", "POST") {
        State.startingGameProp.value = false

        State.gameId.value = it.gameId
        State.guesses.value = it.guesses
        State.gameWon.value = it.result?.won
        State.gameForfeit.value = it.result?.forfeit
        State.word.value = it.word
    }
}