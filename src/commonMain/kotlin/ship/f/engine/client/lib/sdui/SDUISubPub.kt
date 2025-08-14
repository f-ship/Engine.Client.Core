package ship.f.engine.client.lib.sdui

import kotlinx.coroutines.launch
import ship.f.engine.client.core.State
import ship.f.engine.client.core.SubPub
import ship.f.engine.client.lib.sdui.SDUISubPub.SDUIState
import ship.f.engine.client.lib.web.OpenUrlEvent
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig

class SDUISubPub : SubPub<SDUIState>(
    requiredEvents = setOf(),
    nonRequiredEvents = setOf(SDUIScreenConfigEvent::class, SDUIElementConfigEvent::class, SDUIMetaEvent::class, SDUIScreenReferenceEvent::class),
) {
    override fun tempSafeInit() {
        val client = getDependency(CommonClientDependency::class).client
        client.emitConfig = { screenId, metaId, elements, metas ->
            coroutineScope.launch {
                publish(SDUIRequestEvent(screenId, metaId, elements, metas))
            }
        }
        client.emitWebAction = { url ->
            coroutineScope.launch {
                publish(OpenUrlEvent(action = url))
            }
        }
    }

    override fun initState() = SDUIState()
    data class SDUIState(
        val screenConfig: ScreenConfig = ScreenConfig.empty, // Not sure if this is even needed, just needed to fill up state I guess
    ) : State()

    override suspend fun onEvent() {
        le<SDUIScreenConfigEvent> {
            val client = getDependency(CommonClientDependency::class).client
            client.navigate(it.screenConfigs.first())
            state.value = state.value.copy(screenConfig = it.screenConfigs.first())
                .apply { isReady = state.value.isReady } // TODO this is so very bad and janky, should at least hide it with a method!
            if (it.screenConfigs.size > 1) {
                it.screenConfigs.subList(1, it.screenConfigs.size).forEach { screenConfig ->
                    client.store(screenConfig)
                }
            }
        }

        le<SDUIElementConfigEvent> {
            getDependency(CommonClientDependency::class).client.navigate(it.elementConfigs.first())
        }

        le<SDUIMetaEvent> {
            getDependency(CommonClientDependency::class).client.run {
                it.metas.forEach { meta -> store(meta) }
            }
        }

        le<SDUIScreenReferenceEvent> {
            getDependency(CommonClientDependency::class).client.navigate(it.screenReferences.first()) // TODO screen references should surely not be a list
        }
    }
}

