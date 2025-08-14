package ship.f.engine.client.lib.sdui

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import ship.f.engine.client.core.Dependency
import ship.f.engine.shared.utils.serverdrivenui.json.json

class KtorClientDependency : Dependency() {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(
                json = json
            )
        }
    }
    var config: DebugConfig = DebugConfig()
}

data class DebugConfig(
    val isLogin: Boolean = false
)
