package ship.f.engine.client.core

@Suppress("UNCHECKED_CAST")
abstract class MiddleWare<E : Event> {

    private var isInitialized = false
    operator fun invoke(event: Event): E? = process(event as E)

    private fun init() {

    }

    fun tryInit(){
        if (!isInitialized){
            init()
            isInitialized = true
        }
    }

    abstract fun process(event: E): E?
}