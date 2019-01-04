import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.InputEvent

fun Element.getBooleanAttribute(name: String): Boolean {
    val attr = getAttribute(name)
    return attr != null
}

fun Element.setBooleanAttribute(name: String, v: Boolean) {
    if (v) {
        setAttribute(name, "true")
    } else {
        removeAttribute(name)
    }
}

var Element.disabled: Boolean
    get() = getBooleanAttribute("disabled")
    set(v) = setBooleanAttribute("disabled", v)

var Element.hidden: Boolean
    get() = getBooleanAttribute("hidden")
    set(v) = setBooleanAttribute("hidden", v)

fun EventTarget.asInputElement(): HTMLInputElement = this as HTMLInputElement