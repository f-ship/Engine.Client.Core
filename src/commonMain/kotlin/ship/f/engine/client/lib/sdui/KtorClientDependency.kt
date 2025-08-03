package ship.f.engine.client.lib.sdui

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import ship.f.engine.client.core.Dependency

class KtorClientDependency : Dependency() {
    val client = HttpClient()
    var config: DebugConfig = DebugConfig()

    suspend fun greeting(): String {
        val response = client.get("https://ktor.io/docs/")
        return response.bodyAsText()
    }
}

data class DebugConfig(
    val isLogin: Boolean = false
)
