package com.chadrc.hangman

import kotlinx.css.*
import kotlinx.css.properties.borderBottom
import kotlinx.css.properties.borderLeft
import kotlinx.css.properties.borderRight
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
        marginBottom = 20.px
    }

    h1 {
        fontSize = 200.pct
    }

    h2 {
        fontSize = 150.pct
    }

    rule(".new-game-button") {
        margin(LinearDimension.auto)
        display = Display.block
        marginBottom = 10.px
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
        textAlign = TextAlign.center
        margin(0.em, .25.em)
        borderBottom(2.px, BorderStyle.solid, Color.black)
    }

    rule(".hangman-guess-form, .hangman-game-result") {
        borderTop(2.px, BorderStyle.solid, Color.black)
        paddingTop = 20.px
        marginBottom = 20.px
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
    }

    rule(".hangman-guess-form.hidden .hangman-guess-result.hidden") {
        display = Display.none
    }

    rule(".hangman-guess-result") {

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

    rule(".guesses-header") {
        borderBottom(2.px, BorderStyle.solid, Color.black)
        paddingBottom = 10.px
    }

    rule(".guesses-lists") {
        display = Display.flex
        flexDirection = FlexDirection.row
    }

    rule(".guesses-lists > ul") {
        width = 50.pct
    }

    rule(".guesses-lists > ul:first-child") {
        borderRight(1.px, BorderStyle.solid, Color.black)
    }

    rule(".guesses-lists > ul:last-child") {
        borderLeft(1.px, BorderStyle.solid, Color.black)
    }

    rule(".guesses-lists > ul > li:first-child") {
        width = 100.pct
        display = Display.block
        padding(10.px)
    }

    rule(".guesses-lists > ul > li") {
        textAlign = TextAlign.center
        width = 50.pct
        display = Display.inlineBlock
    }

    rule(".hidden") {
        display = Display.none
    }
}
