package ship.f.engine.client.lib.sdui2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import ship.f.engine.client.core.Slice
import ship.f.engine.client.lib.sdui2.SDUISubPub2.SDUIState2
import ship.f.engine.client.utils.serverdrivenui2.ServerDrivenScreen2

object SDUISlice2 : Slice<SDUIState2, SDUISubPub2>(
    subPubClass = SDUISubPub2::class,
) {
    @Composable
    operator fun invoke() = Show()

    @Composable
    override fun EntryPoint(state: MutableState<SDUIState2>) {
        ServerDrivenScreen2(
            projectName = state.value.projectName,
            resources = state.value.resources,
        )
    }

    @Composable
    override fun notReadyEntryPoint(state: MutableState<SDUIState2>): @Composable (() -> Unit) {
        return {
            // No need to render anything here
        }
    }
}