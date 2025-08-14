package ship.f.engine.client.lib.web

import ship.f.engine.client.core.State
import ship.f.engine.client.core.SubPub
import ship.f.engine.client.lib.web.WebSubPub.WebState
import ship.f.engine.shared.utils.serverdrivenui.action.Action

class WebSubPub : SubPub<WebState>(
    nonRequiredEvents = setOf(OpenUrlEvent::class)
) {
    data class WebState(
        val url: Action.SendUrl? = null,
    ) : State()

    override fun initState() = WebState()

    override suspend fun onEvent() {
        le<OpenUrlEvent> {
            state.value = state.value.copy(url = it.action).apply { isReady = state.value.isReady }
        }
    }
}