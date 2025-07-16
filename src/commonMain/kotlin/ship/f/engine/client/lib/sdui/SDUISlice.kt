package ship.f.engine.client.lib.sdui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ship.f.engine.client.core.Slice
import ship.f.engine.client.lib.sdui.SDUISubPub.SDUIState
import ship.f.engine.client.utils.serverdrivenui.RenderScreen


// TODO need to add scope to this to reduce memory overhead of repeated constructing this
class SDUISlice(val projectName: String) : Slice<SDUIState, SDUISubPub>(
    subPubClass = SDUISubPub::class,
) {
    @Composable
    override fun EntryPoint(state: MutableState<SDUIState>) {
        RenderScreen(
            projectName = projectName,
        )
    }

    @Composable
    override fun notReadyEntryPoint(state: MutableState<SDUIState>): @Composable () -> Unit {
        return {
            // No need to render anything here
        }
    }
}