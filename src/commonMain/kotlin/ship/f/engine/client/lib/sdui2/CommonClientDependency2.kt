package ship.f.engine.client.lib.sdui2

import ship.f.engine.client.core.Dependency
import ship.f.engine.client.utils.serverdrivenui2.CommonClient2.Companion.create

class CommonClientDependency2(projectName: String? = null) : Dependency() {
    val client = create(projectName)
}