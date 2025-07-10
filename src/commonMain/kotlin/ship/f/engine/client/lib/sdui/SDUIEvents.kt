package ship.f.engine.client.lib.sdui

import ship.f.engine.client.core.Event
import ship.f.engine.shared.utils.serverdrivenui.ElementConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig

data class SDUIScreenConfigEvent(
    val screenConfig: ScreenConfig,
) : Event()

data class SDUIElementConfigEvent(
    val elementConfig: ElementConfig,
) : Event()