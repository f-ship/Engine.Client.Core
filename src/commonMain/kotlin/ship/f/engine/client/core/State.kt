package ship.f.engine.client.core

abstract class State {
    val isLoading: Boolean = false
    var isReady: Boolean = false
    var publish: Publish? = null // Probably don't even need this
    class NoState : State()
}