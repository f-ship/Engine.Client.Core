package ship.f.engine.client.lib.sdui

import ship.f.engine.client.core.Event
import ship.f.engine.shared.utils.serverdrivenui.ElementOperation
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig

data class SDUIScreenConfigEvent(
    val screenConfig: List<ScreenConfig>,
) : Event()

data class SDUIElementConfigEvent(
    val elementConfig: ElementOperation,
) : Event()