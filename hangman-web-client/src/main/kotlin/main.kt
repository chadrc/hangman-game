import kotlinx.html.*
import kotlinx.html.Entities.nbsp
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.span
import kotlin.browser.document
import kotlin.browser.window

fun main() {
    getGame()
    window.addEventListener("load", {
        val body = document.body!!

        body.append {

            main {
                header {
                    h1 {
                        +"Hangman"
                    }
                }

                button {
                    disableIfGettingGame()
                    hideIfInvalidGameId()

                    onClickFunction = { startGame() }

                    +"Start Game"
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

                                renderWithCharacterGuesses { guesses ->
                                    guesses.map { guess ->
                                        li {
                                            + guess
                                        }
                                    }
                                }
                            }

                            ul("hangman-word-guesses") {
                                li {
                                    +"Words"
                                }

//                                renderWithWordGuesses()
                            }
                        }
                    }
                }
            }
        }

        Binder.bindElements()
    })
}