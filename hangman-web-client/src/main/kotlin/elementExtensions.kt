import org.w3c.dom.Element

var Element.disabled: Boolean
    get() {
        val attr = getAttribute("disabled")
        return attr != null
    }
    set(v) {
        if (v) {
            setAttribute("disabled", "true")
        } else {
            removeAttribute("disabled")
        }
    }