import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import kotlinx.html.js.onInputFunction
import kotlinx.html.onInput
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event

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

fun CommonAttributeGroupFacade.renderWithWord(render: TagConsumer<HTMLElement>.(word: String) -> Unit) {
    bindState(State.word) { _, new ->
        while (firstChild != null) {
            removeChild(firstChild as Node)
        }

        this.append { render.invoke(this, new) }
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
