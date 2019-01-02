package com.chadrc.hangman

import kotlinx.css.*
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.borderTop

val main get() = TagSelector("main")

fun value(number: Number, unit: String): String {
    return if (number == 0)
        "0"
    else
        number.toString() + unit
}

val Number.rem: LinearDimension get() = LinearDimension(value(this, "rem"))

fun CSSBuilder.mainStyles() {

    main {
        width = 70.pct
        margin(LinearDimension.auto)
    }

    header {
        textAlign = TextAlign.center
    }

    button {

    }

    rule(".hangman-game") {
        display = Display.flex
        flexDirection = FlexDirection.column
        justifyContent = JustifyContent.center
    }

    rule(".hangman-word") {
        display = Display.flex
        flexDirection = FlexDirection.row
        justifyContent = JustifyContent.center
        alignItems = Align.center
        marginBottom = 20.px
    }

    rule(".hangman-character") {
        display = Display.block
        fontSize = 2.rem
        width = 1.em
        margin(0.em, .25.em)
        borderBottom(2.px, BorderStyle.solid, Color.black)
    }

    rule(".hangman-guess-form") {
        borderTop(2.px, BorderStyle.solid, Color.black)
        paddingTop = 20.px
        marginBottom = 20.px
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
    }

    rule(".hangman-guess-form input") {
        border = "none"
        borderBottom(2.px, BorderStyle.solid, Color.black)
        fontSize = 2.rem
        width = 10.em
        outline = Outline.none
        marginRight = 10.px
        textAlign = TextAlign.center
    }

    rule(".hangman-guess-form button") {
        marginLeft = 10.px
        outline = Outline.none
    }

    rule(".hangman-guesses") {
        borderTop(2.px, BorderStyle.solid, Color.black)
        paddingTop = 20.px
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    rule(".guesses-lists") {
        display = Display.flex
        flexDirection = FlexDirection.row
    }

    rule(".guesses-lists > ul") {
        width = 50.pct
    }

    rule(".guesses-lists > ul > li") {

    }

    rule(".guesses-lists > ul > li:first-child") {
        textAlign = TextAlign.center
    }
}
