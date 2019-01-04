// (oldValue, newValue) -> Unit
typealias ObservablePropCallback<T> = (T, T) -> Unit

class ObservableProp<T>(initialValue: T) {
    private var currentValue = initialValue
    private val callbacks: MutableList<ObservablePropCallback<T>> = mutableListOf()

    var value: T
        get() = currentValue
        set(value) {
            val old = currentValue
            currentValue = value

            for (cb in callbacks) {
                try {
                    cb(old, currentValue)
                } catch (exception: Exception) {
                    // don't want to interrupt notifying callbacks
                    // simply log
                    console.error("Error during observable callback", exception)
                }
            }
        }

    fun onChange(callback: ObservablePropCallback<T>) {
        callbacks.add(callback)
    }
}

object State {
    val gettingGame = ObservableProp(false)
    val makingGuess = ObservableProp(false)
    val forfeiting = ObservableProp(false)

    val guessText = ObservableProp("")

    val gameId = ObservableProp(-1)
    val guesses= ObservableProp(listOf<String>())
    val word = ObservableProp("")
    val gameWon = ObservableProp<Boolean?>(null)
    val gameForfeit = ObservableProp<Boolean?>(null)
}