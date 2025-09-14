package ship.f.engine.client.lib.web

import ship.f.engine.shared.core.Event
import ship.f.engine.shared.utils.serverdrivenui.action.Action

data class OpenUrlEvent(
    val action: Action.SendUrl,
) : Event()

data class ReturnCodeEvent(
    val code: String, // TODO will probably need something more elaborate, mabe even scoping
) : Event()