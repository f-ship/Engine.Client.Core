package ship.f.engine.client.lib.sdui

import ship.f.engine.client.core.State
import ship.f.engine.client.core.SubPub
import ship.f.engine.client.lib.sdui.SDUISubPub.SDUIState
import ship.f.engine.shared.utils.serverdrivenui.ScreenConfig

class SDUISubPub : SubPub<SDUIState>(
    requiredEvents = setOf(),
    nonRequiredEvents = setOf(SDUIScreenConfigEvent::class, SDUIElementConfigEvent::class),
) {
    override fun init() {
        engine.addDependency(CommonClientDependency::class, CommonClientDependency())
    }

    override fun initState() = SDUIState()
    data class SDUIState(
        val screenConfig: ScreenConfig = ScreenConfig.empty, // Not sure if this is even needed, just needed to fill up state I guess
    ) : State()

    override suspend fun onEvent() {
        le<SDUIScreenConfigEvent> {
            val client = getDependency(CommonClientDependency::class).client
            client.navigate(it.screenConfig.first())
            state.value = state.value.copy(screenConfig = it.screenConfig.first())
                .apply { isReady = state.value.isReady } // TODO this is so very bad and janky, should at least hide it with a method!
            if (it.screenConfig.size > 1) {
                it.screenConfig.subList(1, it.screenConfig.size).forEach { screenConfig ->
                    client.store(screenConfig)
                }
            }
        }

        le<SDUIElementConfigEvent> {
            getDependency(CommonClientDependency::class).client.navigate(it.elementConfig)
        }
    }
}

