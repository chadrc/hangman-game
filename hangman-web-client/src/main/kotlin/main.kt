import kotlinx.html.button
import kotlinx.html.dom.append
import kotlinx.html.h1
import kotlinx.html.header
import kotlinx.html.js.onClickFunction
import kotlinx.html.main
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    getGame()
    window.addEventListener("load", {
        val body = document.body!!

        body.append {
            header {
                h1 {
                    +"Hangman"
                }
            }

            main {
                button {
                    bindState(State.gettingGame) { _, new ->
                        disabled = new
                    }

                    bindState(State.gameId) { _, new ->
                        hidden = new != -1
                    }

                    onClickFunction = { startGame() }

                    +"Start Game"
                }
            }
        }

        Binder.bindElements()
    })
}