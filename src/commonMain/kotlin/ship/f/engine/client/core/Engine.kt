package ship.f.engine.client.core

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST") //Should probably create extensions for safe map access
object Engine {
    private var config = Config()

    fun <E : Event> getEvent(
        event: KClass<E>,
        scope: ScopeTo
    ): E? {
        val a = config.eventMiddleWareConfig[event]!! // All events should be configured before engine is ran
        val b = a.eventConfigs[scope]
        val c = b?.event
        return c as? E
    }

    fun init(config: Config) {
        this.config = config
    }

    suspend fun publish(event: E, reason: String) { // Do something with reason
        println("Engine Publishing $event for $reason")
        val middleWares = config.eventMiddleWareConfig[event::class]!!.middleWareConfigs.map { it.listener }
        var computedEvent = event
        middleWares.forEach { middleWare ->
            computedEvent = middleWare(computedEvent) ?: computedEvent
        }

        (computedEvent.getScopes() + listOf(defaultScope)).forEach { scope ->
            val eventConfigs = config.eventMiddleWareConfig[computedEvent::class]!!.eventConfigs
            eventConfigs[scope] =
                eventConfigs[scope]?.copy(event = computedEvent) ?: EventConfig(computedEvent, setOf())
            eventConfigs[scope]!!.listeners.forEach {
                it.onEvent()
            }
        }
    }

    fun addScopes(subPub: SP, scope: ScopeTo, events: List<EClass>) {
        events.forEach {
            val eventConfigs = config.eventMiddleWareConfig[it]!!.eventConfigs
            val listeners = eventConfigs[scope]?.listeners ?: setOf()
            eventConfigs[scope] =
                eventConfigs[scope]?.copy(listeners = listeners + listOf(subPub)) ?: EventConfig(
                    event = null,
                    listeners = listeners
                )
        }
    }

    fun <SP : SubPub<out State>> getSubPub(subPubClass: KClass<out SP>, scope: ScopeTo): SP {
        val a = config.subPubConfig[subPubClass]!!
        val b = a.subPubs[scope] ?: a.build().apply { tryInit() }
        addScopes(b, scope, b.events.toList())
        return b as SP
    }

    fun <D : Dependency> getDependency(dependency: KClass<out D>, scope: ScopeTo): D {
        val a = config.dependencyConfig[dependency]!!
        val b = a.dependencies[scope] ?: a.build(scope).apply { init(scope) }
        return b as D
    }
}

data class Config(
    val subPubConfig: Map<SPClass, SubPubConfig> = mapOf(),
    val eventMiddleWareConfig: Map<EClass, EventMiddleWareConfig> = mapOf(),
    val dependencyConfig: Map<DClass, DependencyConfig> = mapOf(),
)

data class SubPubConfig(
    val build: () -> SP,
    val subPubs: Map<ScopeTo, SP> = mapOf(), // Do I really need this one here
)

data class EventMiddleWareConfig(
    val eventConfigs: MutableMap<ScopeTo, EventConfig> = mutableMapOf(),
    val middleWareConfigs: List<MiddleWareConfig> = listOf(), // For now Assume all middlewares are created at init
)

data class EventConfig(
    val event: E?, //Can be null as someone can scope to an event that has not been published yet
    val listeners: Set<SP> = setOf(),
)

data class MiddleWareConfig(
    val build: () -> MW,
    val listener: MW,
)

data class DependencyConfig(
    val build: (ScopeTo) -> D,
    val dependencies: Map<ScopeTo, D> = mapOf(),
)

typealias SP = SubPub<out State>
typealias SPClass = KClass<out SP>

typealias MW = MiddleWare<out Event>

typealias E = Event
typealias EClass = KClass<out Event>

typealias D = Dependency
typealias DClass = KClass<out D>

typealias Publish = (E, String?, String) -> Unit
