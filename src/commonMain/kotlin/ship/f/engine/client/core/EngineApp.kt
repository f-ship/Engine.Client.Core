package ship.f.engine.client.core

import androidx.compose.runtime.*

@Composable
fun EngineApp(
    config: Config = Config(),
    initialEvent: ScopedEvent? = null,
    content: @Composable () -> Unit,
) {
    var hasBeenInit by remember { mutableStateOf(false) }

    LaunchedEffect(config) {
        Engine.init(config, initialEvent)
        hasBeenInit = Engine.hasBeenInit
    }

    if (hasBeenInit) {
        content()
    }
}