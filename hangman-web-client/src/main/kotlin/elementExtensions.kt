import org.w3c.dom.Element

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