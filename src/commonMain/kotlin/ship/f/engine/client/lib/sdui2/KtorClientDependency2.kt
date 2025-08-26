package ship.f.engine.client.lib.sdui2

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import ship.f.engine.client.core.Dependency
import ship.f.engine.shared.utils.serverdrivenui2.json.json2

class KtorClientDependency2 : Dependency() {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(
                json = json2
            )
        }
    }
}