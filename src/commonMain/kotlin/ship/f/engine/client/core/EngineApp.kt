package ship.f.engine.client.core

import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EngineApp(
    config: Config = Config(),
    initialEvent: ScopedEvent? = null,
    content: @Composable () -> Unit,
) {
    var hasBeenInit by remember { mutableStateOf(false) }

    LaunchedEffect(config) {
        if (!Engine.hasBeenInit) {
            Engine.init(config, initialEvent)
        }
        withContext(Dispatchers.Main) {
            hasBeenInit = Engine.hasBeenInit
        }
        initialEvent?.let { Engine.publish(it, "Initial Event") }
    }

    if (hasBeenInit) {
        content()
    }
}