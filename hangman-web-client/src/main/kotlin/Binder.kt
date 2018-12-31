import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.id
import org.w3c.dom.Element
import kotlin.browser.document

object IdGenerator {
    var currentId: Int = 0

    val id: String
        get() = currentId++.toString()
}

typealias BindingFunc<T> = Element.(T, T)-> Unit

class Binding<T>(
    val id: String,
    val prop: ObservableProp<T>,
    val func: BindingFunc<T>
)

object Binder {
    private val registry: MutableList<Binding<dynamic>> = mutableListOf()

    fun <T> registerBinding(id: String, prop: ObservableProp<T>, func: BindingFunc<T>) {
        registry.add(Binding(id, prop, func))
    }

    fun bindElements() {
        for (binding in registry) {
            val element = document.getElementById(binding.id)
            if (element == null) {
                console.error("No element with id ${binding.id} found to bind")
                continue
            }

            binding.prop.onChange { old, new ->
                binding.func.invoke(element, old, new)
            }
        }
    }
}

fun <T> CommonAttributeGroupFacade.bind(
    prop: ObservableProp<T>,
    func: BindingFunc<T>
) {
    id = "binding-${IdGenerator.id}"
    Binder.registerBinding(id, prop, func)
}