import kotlinx.html.*
import kotlinx.html.Entities.nbsp
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.span
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.browser.window

val renderStringList: TagConsumer<HTMLElement>.(List<String>) -> Unit = { list ->
    list.map {
        li {
            + it
        }
    }
}

fun main() {
    window.addEventListener("load", {
        val body = document.body!!

        body.append {

            main {
                header {
                    h1 {
                        +"Hangman"
                    }
                }

                button(classes = "new-game-button") {
                    disableIfGettingGame()
                    hideIfGameInProgress(rerender = false)

                    onClickFunction = { startGame() }

                    +"New Game"
                }

                section("hangman-game") {
                    showIfValidGameId()

                    section("hangman-word") {
                        renderWithWord { word ->
                            for (c in word) {
                                span("hangman-character") {
                                    if (c == ' ') {
                                        + nbsp
                                    } else  {
                                        + c.toString()
                                    }
                                }
                            }
                        }
                    }

                    section("hangman-guess-form") {
                        showIfGameInProgress {
                            input {
                                placeholder = "Guess"

                                bindGuessTextValueInput()
                            }

                            button {
                                +"Guess"

                                onClickFunction = { makeGuess() }
                            }

                            button {
                                +"Forfeit"

                                onClickFunction = { forfeitGame() }
                            }
                        }
                    }

                    section("hangman-game-result") {
                        hideIfGameInProgress {
                            val won = State.gameWon.value
                            val forfeit = State.gameForfeit.value
                            val text = if (won == null) {
                                if (forfeit == true) "Forfeit" else ""
                            } else {
                                if (won) "Won" else "Lost"
                            }

                            h2 {
                                +text
                            }
                        }
                    }

                    section("hangman-guesses") {
                        header("guesses-header") {
                            h2 {
                                +"Guesses"
                            }
                        }

                        section("guesses-lists") {
                            ul("hangman-character-guesses") {
                                li {
                                    +"Letters"
                                }

                                renderWithCharacterGuesses(renderStringList)
                            }

                            ul("hangman-word-guesses") {
                                li {
                                    +"Words"
                                }

                                renderWithWordGuesses(renderStringList)
                            }
                        }
                    }
                }
            }
        }

        Binder.bindElements()

        getGame()
    })
}