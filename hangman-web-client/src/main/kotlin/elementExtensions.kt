import org.w3c.dom.Element

var Element.disabled: Boolean
    get() = getAttribute("disabled")?.toBoolean() ?: false
    set(v) = setAttribute("disabled", v.toString())