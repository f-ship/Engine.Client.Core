package ship.f.engine.client.core

abstract class Dependency : SubPub<State.NoState>() { // While a dependency inherits from a subpub it should not be used to listen to events, should split
    open fun init(scope: ScopeTo){

    }
}