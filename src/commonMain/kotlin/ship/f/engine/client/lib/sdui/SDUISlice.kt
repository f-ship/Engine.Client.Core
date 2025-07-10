package ship.f.engine.client.lib.sdui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ship.f.engine.client.core.Slice
import ship.f.engine.client.lib.sdui.SDUISubPub.SDUIState
import ship.f.engine.client.utils.serverdrivenui.RenderScreen


// TODO need to add scope to this to reduce memory overhead of repeated constructing this
class SDUISlice : Slice<SDUIState, SDUISubPub>(
    subPubClass = SDUISubPub::class,
) {
    @Composable
    override fun EntryPoint(state: MutableState<SDUIState>) {
        RenderScreen(
//            screenConfig = mutableStateOf(state.value.screenConfig) May not need to re-enable this for now
        )
    }

    @Composable
    override fun NotReadyEntryPoint(state: MutableState<SDUIState>): @Composable () -> Unit {
        return {
            // No need to render anything here
        }
    }
}