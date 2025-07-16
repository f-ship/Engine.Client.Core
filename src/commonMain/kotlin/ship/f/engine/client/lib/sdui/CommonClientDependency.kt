package ship.f.engine.client.lib.sdui

import ship.f.engine.client.core.Dependency
import ship.f.engine.client.utils.serverdrivenui.CommonClient

class CommonClientDependency(projectName: String? = null) : Dependency() {
    val client = CommonClient.getClient(projectName)
}