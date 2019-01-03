import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import org.w3c.dom.HTMLElement
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

fun CommonAttributeGroupFacade.renderWithWord(render: TagConsumer<HTMLElement>.(word: String) -> Unit) {
    bindState(State.word) { _, new ->
        while (firstChild != null) {
            removeChild(firstChild as Node)
        }

        this.append { render.invoke(this, new) }
    }
}