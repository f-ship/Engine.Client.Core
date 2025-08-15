package ship.f.engine.client.core

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
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
    var lastEvent: E = ScopedEvent.InitialEvent(uid)
    lateinit var state: MutableState<S>
    private val idempotentMap: MutableMap<EClass, MutableSet<String>> = mutableMapOf()
    protected val coroutineScope: CoroutineScope = engine.engineScope

    val linkedExpectations = mutableMapOf<Pair<EClass, String?>, LinkedExpectation>()

    abstract fun initState(): S
    abstract suspend fun onEvent()

    // TODO really is another awful method right here, that being said this single handedly enables me to handle sync work
    suspend fun executeEvent() {
//        getEvent(lastEvent::class)?.let { lastEvent ->
//            linkedExpectations.forEach { linkedExpectation ->
//                val blockedEvents = mutableSetOf<EClass>()
//                val allList = linkedExpectation.value.all
//                val updatedAllList = allList.map {
//                    blockedEvents.add(it.first.expectedEvent)
//                    if (it.first.expectedEvent == lastEvent::class) Pair(it.first, true) else it
//                }
//                if (updatedAllList.all { it.second }) {
//                    updatedAllList.forEach { expectation ->
//                        val event = getEvent(expectation.first.expectedEvent)!!
//                        expectation.first.on(event)
//                        blockedEvents.remove(expectation.first.expectedEvent)
//                    }
//                    linkedExpectations.remove(linkedExpectation.key)
//                }
//
//                val anyList = linkedExpectation.value.any
//                for (any in anyList) {
//                    if (any.expectedEvent == lastEvent::class && !blockedEvents.contains(any.expectedEvent)) {
//                        any.on(lastEvent)
//                        linkedExpectations.remove(linkedExpectation.key)
//                        break
//                    }
//                }
//            }
//        }
        onEvent()
    }

    private var isInitialized = false //Can probably remove

    open fun init() {

    }

    // Can safely run as much as needed as idempotent
    open fun tempSafeInit() {

    }

    fun tryInit() {
        if (!isInitialized) {
            init()
            println("SubPub $uid is initialized")
            state = mutableStateOf(initState().apply { isReady = checkIfReady() })
            isInitialized = true
        }
        tempSafeInit()
    }

    suspend fun publish(
        event: E,
        key: String? = null,
        reason: String = "Please Give a Reason for readability"
    ): Expectation {
        idempotentMap[event::class]?.contains(key) ?: let {
            idempotentMap.smartAdd(event::class, key)
            engine.publish(event, reason)
        }
        return Expectation(emittedEvent = event, key = key, expectedEvent = null)
    }

    // Pair<EClass, String?> is only used to stop multiple of the same item being added.
    // Ultimately, we will still iterate through the entire list

    fun Expectation.onceAny(vararg expectationBuilders: ExpectationBuilder) {
        if (linkedExpectations.contains(Pair(emittedEvent::class, key))) return
        val any = mutableListOf<ExpectationBuilder>()
        expectationBuilders.forEach {
            any.add(it)
        }
        val linkedExpectation = LinkedExpectation(
            any = any,
            all = listOf(),
        )
        linkedExpectations[Pair(emittedEvent::class, key)] = linkedExpectation
    }

    fun Expectation.onceAll(vararg expectationBuilders: ExpectationBuilder) {
        if (linkedExpectations.contains(Pair(emittedEvent::class, key))) return
        val all = mutableListOf<Pair<ExpectationBuilder, Boolean>>()
        expectationBuilders.forEach {
            all.add(Pair(it, false))
        }
        val linkedExpectation = LinkedExpectation(
            any = listOf(),
            all = all,
        )
        linkedExpectations[Pair(emittedEvent::class, key)] = linkedExpectation
    }

    fun getEvent(event: EClass): ScopedEvent? =
        getScopedEvent(event, scopes.last { it.first.mode == ScopeMode.Instance }.first)

    fun <E : ScopedEvent> getScopedEvents(event: KClass<out E>, scope: ScopeTo? = null): List<E> =
        scopes.filter { //Should probably be a set...
            (it.second?.contains(event) == true) && (scope == null || scope == it.first)
        }.mapNotNull {
            getScopedEvent(event, it.first)
        }

    private fun <E : ScopedEvent> getScopedEvent(event: KClass<out E>, scope: ScopeTo): E? =
        engine.getEvent(event, scope)

    private fun addScopeOrModify(
        scope: ScopeTo,
        events: List<EClass> = this.events.toList()
    ) { //I think I need to somehow think it through, I don't understand what null is? All events? nah that is stupid should just default to all events
        engine.addScopes(this, scope, events) //This is done to modify the event config at runtime
        scopes = scopes.map {
            if (it.first == scope) {
                it.copy(second = events)
            } else {
                it
            }
        }
    }

    fun <D : Dependency> getDependency( // TODO should make this inline
        dependency: KClass<out D>,
        scope: ScopeTo = defaultScope
    ): D {
        if (!isInitialized) {
            init()
        } // TODO this is currently done because of layout inspector destroying state randomly
        return engine.getDependency(dependency, scope)
    }

    private fun checkIfReady(runIfNotReady: () -> Unit = {}) = requiredEvents.none {
        getEvent(it) == null
    }.also {
        if (!it) {
            runIfNotReady()
        }
    } //Add a method that enables work to be done to mitigate this to get the subpub up and running

    inline fun <reified E1 : E> SubPub<S>.ge(func: (E1) -> Unit, nFunc: () -> Unit = {}) {
        getEvent(E1::class)?.also { func(it as E1) } ?: nFunc()
    }

    inline fun <reified E1 : E> SubPub<S>.ges(
        func: (List<E>) -> Unit,
        nFunc: () -> Unit = {},
        scopeTo: ScopeTo? = null
    ) {
        getScopedEvents(E1::class, scopeTo).also {
            if (it.isNotEmpty()) {
                func(it)
            } else {
                nFunc()
            }
        }
    }

    inline fun <reified E1 : E, reified E2 : E> SubPub<S>.ge2(func: (E1?, E2?) -> Unit, nFunc: () -> Unit = {}) {
        val e1 = getEvent(E1::class)
        val e2 = getEvent(E2::class)
        if (e1 != null || e2 != null) {
            func(e1 as? E1, e2 as? E2)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E, reified E2 : E> SubPub<S>.ges2(
        func: (List<E1>, List<E2>) -> Unit,
        nFunc: () -> Unit = {},
        scopeTo: ScopeTo? = null
    ) {
        val e1 = getScopedEvents(E1::class, scopeTo)
        val e2 = getScopedEvents(E2::class, scopeTo)
        if (e1.isNotEmpty() || e2.isNotEmpty()) {
            func(e1, e2)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E, reified E2 : E> SubPub<S>.gae2(func: (E1, E2) -> Unit, nFunc: () -> Unit = {}) {
        val e1 = getEvent(E1::class)
        val e2 = getEvent(E2::class)
        if (e1 is E1 && e2 is E2) {
            func(e1, e2)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E, reified E2 : E> SubPub<S>.gaes2(
        func: (List<E1>, List<E2>) -> Unit,
        nFunc: () -> Unit = {},
        scopeTo: ScopeTo? = null
    ) {
        val e1 = getScopedEvents(E1::class, scopeTo)
        val e2 = getScopedEvents(E2::class, scopeTo)
        if (e1.isNotEmpty() && e2.isNotEmpty()) {
            func(e1, e2)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E, reified E2 : E, reified E3 : E> SubPub<S>.ge3(
        func: (E1?, E2?, E3?) -> Unit,
        nFunc: () -> Unit = {}
    ) {
        val e1 = getEvent(E1::class)
        val e2 = getEvent(E2::class)
        val e3 = getEvent(E2::class)
        if (e1 != null || e2 != null || e3 != null) {
            func(e1 as? E1, e2 as? E2, e3 as? E3)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E, reified E2 : E, reified E3 : E> SubPub<S>.ges3(
        func: (List<E1>, List<E2>, List<E3>) -> Unit,
        nFunc: () -> Unit = {},
        scopeTo: ScopeTo? = null
    ) {
        val e1 = getScopedEvents(E1::class, scopeTo)
        val e2 = getScopedEvents(E2::class, scopeTo)
        val e3 = getScopedEvents(E3::class, scopeTo)
        if (e1.isNotEmpty() || e2.isNotEmpty() || e3.isNotEmpty()) {
            func(e1, e2, e3)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E, reified E2 : E, reified E3 : E> SubPub<S>.gea3(
        func: (E1, E2, E3) -> Unit,
        nFunc: () -> Unit = {}
    ) {
        val e1 = getEvent(E1::class)
        val e2 = getEvent(E2::class)
        val e3 = getEvent(E2::class)
        if (e1 is E1 && e2 is E2 && e3 is E3) {
            func(e1, e2, e3)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E, reified E2 : E, reified E3 : E> SubPub<S>.geas3(
        func: (List<E1>, List<E2>, List<E3>) -> Unit,
        nFunc: () -> Unit = {},
        scopeTo: ScopeTo? = null
    ) {
        val e1 = getScopedEvents(E1::class, scopeTo)
        val e2 = getScopedEvents(E2::class, scopeTo)
        val e3 = getScopedEvents(E3::class, scopeTo)
        if (e1.isNotEmpty() && e2.isNotEmpty() && e3.isNotEmpty()) {
            func(e1, e2, e3)
        } else {
            nFunc()
        }
    }

    inline fun <reified E1 : E> le(func: (E1) -> Unit) {
        val le = lastEvent
        if (le is E1) func(le)
    }

    inline fun <reified E1 : E, reified E2 : E> le2(func: (E1?, E2?) -> Unit) {
        when (val le = lastEvent) {
            is E1 -> func(le, null)
            is E2 -> func(null, le)
        }
    }

    inline fun <reified E1 : E, reified E2 : E, reified E3 : E> le3(func: (E1?, E2?, E3?) -> Unit) {
        when (val le = lastEvent) {
            is E1 -> func(le, null, null)
            is E2 -> func(null, le, null)
            is E3 -> func(null, null, le)
        }
    }
}
