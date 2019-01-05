import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import kotlinx.html.js.onInputFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node

fun CommonAttributeGroupFacade.disableIfGettingGame() {
    bindState(State.gettingGame) { _, new ->
        disabled = new
    }
}

fun CommonAttributeGroupFacade.hideIfInvalidGameId() {
    bindState(State.gameId) { _, new ->
        hidden = new != -1
    }
}

fun CommonAttributeGroupFacade.showIfValidGameId() {
    bindState(State.gameId) { _, new ->
        hidden = new == -1
    }
}

fun CommonAttributeGroupFacade.showIfGameInProgress(render: TagConsumer<HTMLElement>.() -> Unit = {}) {
    bindState(State.gameWon, State.gameForfeit) { _, _ ->
        console.log("change", State.gameWon.value, State.gameForfeit.value)
        if (!State.gameInProgess) {
            classList.add("hidden")
        } else {
            classList.remove("hidden")
        }

        while (firstChild != null) {
            removeChild(firstChild as Node)
        }

        this.append { render.invoke(this) }
    }
}

fun CommonAttributeGroupFacade.hideIfGameInProgress(render: TagConsumer<HTMLElement>.() -> Unit = {}) {
    bindState(State.gameWon, State.gameForfeit) { _, _ ->
        if (State.gameInProgess) {
            classList.add("hidden")
        } else {
            classList.remove("hidden")
        }

        while (firstChild != null) {
            removeChild(firstChild as Node)
        }

        this.append { render.invoke(this) }
    }
}

fun CommonAttributeGroupFacade.renderWithWord(render: TagConsumer<HTMLElement>.(word: String) -> Unit) {
    bindState(State.word) { _, new ->
        while (firstChild != null) {
            removeChild(firstChild as Node)
        }

        this.append { render.invoke(this, new) }
    }
}

fun CommonAttributeGroupFacade.renderWithCharacterGuesses(
    render: TagConsumer<HTMLElement>.(
        word: List<String>
    ) -> Unit
) =
    renderWithGuesses({ State.characterGuesses }, render)

fun CommonAttributeGroupFacade.renderWithWordGuesses(
    render: TagConsumer<HTMLElement>.(
        word: List<String>
    ) -> Unit
) =
    renderWithGuesses({ State.wordGuesses }, render)

private fun CommonAttributeGroupFacade.renderWithGuesses(
    getList: () -> List<String>,
    render: TagConsumer<HTMLElement>.(word: List<String>) -> Unit
) {
    bindState(State.guesses) { _, _ ->
        while (childNodes.length > 1) {
            removeChild(lastChild as Node)
        }

        this.append { render.invoke(this, getList()) }
    }
}

fun CommonAttributeGroupFacade.bindValue(prop: ObservableProp<String>) {
    bindState(prop) { _, new ->
        val inputElement = this as HTMLInputElement

        inputElement.value = new
    }
}

fun CommonAttributeGroupFacade.updateGuessTextOnInput() {
    onInputFunction = {
        updateGuessText(it.target?.asInputElement()?.value ?: "")
    }
}

fun CommonAttributeGroupFacade.bindGuessTextValueInput() {
    bindValue(State.guessText)
    updateGuessTextOnInput()
}
