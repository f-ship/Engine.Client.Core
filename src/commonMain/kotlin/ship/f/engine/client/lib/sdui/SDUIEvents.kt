package ship.f.engine.client.lib.sdui

import ship.f.engine.shared.core.Event
import ship.f.engine.shared.utils.serverdrivenui.ElementOperation
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig.*
import ship.f.engine.shared.utils.serverdrivenui.action.Meta
import ship.f.engine.shared.utils.serverdrivenui.state.State

data class SDUIScreenConfigEvent(
    val screenConfig: ScreenConfig,
) : Event()

data class SDUIScreenStoreEvent(
    val screenConfigs: List<ScreenConfig>,
) : Event()

data class SDUIElementConfigEvent(
    val elementConfigs: List<ElementOperation>,
) : Event()

data class SDUIMetaEvent(
    val metas: List<Meta>,
) : Event()

data class SDUIScreenReferenceEvent(
    val screenReferences: List<ScreenId>,
) : Event()

data class SDUIRequestEvent(
    val screenId: ScreenId,
    val metaId: MetaId,
    val elements: List<Element<out State>>,
    val metas: List<Meta>,
    val expected: List<ID> = listOf(),
) : Event()
