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

        le<SDUIScreenConfigEvent>{
            getDependency(CommonClientDependency::class).client.navigate(it.screenConfig)
//            withContext(Dispatchers.Main) {
                state.value = state.value.copy(screenConfig = it.screenConfig).apply { isReady = state.value.isReady } // TODO this is so very bad and janky!
//            }
        }

        le<SDUIElementConfigEvent>{
            getDependency(CommonClientDependency::class).client.navigate(it.elementConfig)
        }
    }
}

