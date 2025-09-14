package ship.f.engine.client.core

import androidx.compose.runtime.Composable
import ship.f.engine.client.utils.serverdrivenui2.ext.BlockingLaunchedEffect2
import ship.f.engine.shared.core.Config
import ship.f.engine.shared.core.Engine.init
import ship.f.engine.shared.core.ScopedEvent

@Composable
fun EngineApp(
    config: Config = Config(),
    initialEvents: List<ScopedEvent> = listOf(),
    content: @Composable () -> Unit,
) {
    BlockingLaunchedEffect2 {
        init(
            config = config,
            initialEvents = initialEvents,
        )
    }
    content()
}