package ship.f.engine.client.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import kotlin.reflect.KClass

abstract class Slice<S : State, SP : SubPub<S>>(
    subPubClass: KClass<out SP>,
) {
    private var scope: ScopeTo = defaultScope
    private val engine: Engine = Engine

    fun withScope(scope: ScopeTo) = this.apply { this.scope = scope }

    @Composable
    abstract fun EntryPoint(state: MutableState<S>)

    @Composable
    abstract fun NotReadyEntryPoint(state: MutableState<S>): @Composable () -> Unit //Probably not needed anymore as should be handled on the sub pub itself

    private val subPub: SP = engine.getSubPub(subPubClass, scope)

    private val publish = subPub::publish

    fun publishOnce(event: E, reason: String) {
        publish(event, "", reason)
    }

    @Composable
    fun show() {
        EntryPoint(subPub.state)
    }

    @Composable
    private fun EntryPointWrapper(state: MutableState<S>) {
        if (state.value.isReady) {
            EntryPoint(state)
        } else {
            NotReadyEntryPoint(state)()
        }
    }
}
