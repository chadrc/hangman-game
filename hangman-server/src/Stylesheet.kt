package com.chadrc.hangman

import kotlinx.css.*
import kotlinx.css.properties.borderBottom

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

    section {
        display = Display.flex
        justifyContent = JustifyContent.center
    }

    rule("section p") {
        display = Display.flex
        justifyContent = JustifyContent.center
        alignItems = Align.center
    }

    rule(".hangman-character") {
        display = Display.block
        fontSize = 2.rem
        width = 1.em
        margin(0.em, .25.em)
        borderBottom(2.px, BorderStyle.solid, Color.black)
    }
}
