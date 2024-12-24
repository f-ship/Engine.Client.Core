package ship.f.engine.client.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import ship.f.engine.client.core.ScopeTo.SingleScopeTo
import kotlin.reflect.KClass

abstract class SubPub<S : State>(
    private val requiredEvents: Set<EClass> = setOf(),
    nonRequiredEvents: Set<EClass> = setOf()
) {
    private val uid = "${this::class.simpleName}:${Clock.System.now()}"

    private var scopes: List<Pair<ScopeTo, List<EClass>?>> = listOf(Pair(SingleScopeTo(), null)) // Change to
    val engine: Engine = Engine
    val events = requiredEvents + nonRequiredEvents
    var lastEvent: E = Event.InitialEvent(uid)
    lateinit var state: MutableState<S>
    private val idempotentMap: MutableMap<EClass, MutableSet<String>> = mutableMapOf()
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    abstract fun initState(): S
    abstract suspend fun onEvent()
    private var isInitialized = false //Can probably remove

    open fun init() {

    }

    fun tryInit() {
        if (!isInitialized) {
            init()
            println("SubPub $uid is initialized")
            state = mutableStateOf(initState().apply { isReady = checkIfReady() })
            isInitialized = true
        }
    }

    fun publish(
        event: E,
        key: String? = null,
        reason: String = "Please Give a Reason for readability"
    ) {
        coroutineScope.launch {
            idempotentMap[event::class]?.contains(key) ?: suspend {
                idempotentMap.smartAdd(event::class, key)
                engine.publish(event, reason)
            }
        }
    }

    fun getEvent(event: EClass): Event? =
        getScopedEvent(event, scopes.last { it.first.mode == ScopeMode.Instance }.first)

    fun <E : Event> getScopedEvents(event: KClass<out E>, scope: ScopeTo? = null): List<E> = scopes.filter { //Should probably be a set...
        (it.second?.contains(event) == true) && (scope == null || scope == it.first)
    }.mapNotNull {
        getScopedEvent(event, it.first)
    }

    private fun <E : Event> getScopedEvent(event: KClass<out E>, scope: ScopeTo): E? =
        engine.getEvent(event, scope)

    private fun addScopeOrModify(scope: ScopeTo, events: List<EClass> = this.events.toList()) { //I think I need to somehow think it through, I don't understand what null is? All events? nah that is stupid should just default to all events
        engine.addScopes(this, scope, events) //This is done to modify the event config at runtime
        scopes = scopes.map {
            if (it.first == scope) {
                it.copy(second = events)
            } else {
                it
            }
        }
    }

    fun <D : Dependency> getDependency(
        dependency: KClass<out D>,
        scope: ScopeTo = defaultScope
    ): D = engine.getDependency(dependency, scope)

    private fun checkIfReady(runIfNotReady: () -> Unit = {}) = requiredEvents.none {
        getEvent(it) == null
    }.also {
        if (!it) {
            runIfNotReady()
        }
    } //Add a method that enables work to be done to mitigate this to get the subpub up and running
}

