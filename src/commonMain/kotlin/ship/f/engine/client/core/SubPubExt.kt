package ship.f.engine.client.core

inline fun <reified E1: E, S: State>SubPub<S>.ge(func: (E1) -> Unit, nFunc: () -> Unit = {}){
    getEvent(E1::class)?.also { func(it as E1) } ?: nFunc()
}

inline fun <reified E1: E, S: State>SubPub<S>.ges(func: (List<E>) -> Unit, nFunc: () -> Unit = {}, scopeTo: ScopeTo? = null){
    getScopedEvents(E1::class, scopeTo).also {
        if (it.isNotEmpty()) {
            func(it)
        } else {
            nFunc()
        }
    }
}

inline fun <reified E1: E, reified E2: E, S: State>SubPub<S>.ge2(func: (E1?, E2?) -> Unit, nFunc: () -> Unit = {}){
    val e1 = getEvent(E1::class)
    val e2 = getEvent(E2::class)
    if (e1 != null || e2 != null){
        func(e1 as? E1, e2 as? E2)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, reified E2: E, S: State>SubPub<S>.ges2(func: (List<E1>, List<E2>) -> Unit, nFunc: () -> Unit = {}, scopeTo: ScopeTo? = null){
    val e1 = getScopedEvents(E1::class, scopeTo)
    val e2 = getScopedEvents(E2::class, scopeTo)
    if (e1.isNotEmpty() || e2.isNotEmpty()){
        func(e1, e2)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, reified E2: E, S: State>SubPub<S>.gae2(func: (E1, E2) -> Unit, nFunc: () -> Unit = {}){
    val e1 = getEvent(E1::class)
    val e2 = getEvent(E2::class)
    if (e1 is E1 && e2 is E2){
        func(e1, e2)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, reified E2: E, S: State>SubPub<S>.gaes2(func: (List<E1>, List<E2>) -> Unit, nFunc: () -> Unit = {}, scopeTo: ScopeTo? = null){
    val e1 = getScopedEvents(E1::class, scopeTo)
    val e2 = getScopedEvents(E2::class, scopeTo)
    if (e1.isNotEmpty() && e2.isNotEmpty()){
        func(e1, e2)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, reified E2: E, reified E3: E, S: State>SubPub<S>.ge3(func: (E1?, E2?, E3?) -> Unit, nFunc: () -> Unit = {}){
    val e1 = getEvent(E1::class)
    val e2 = getEvent(E2::class)
    val e3 = getEvent(E2::class)
    if (e1 != null || e2 != null || e3 != null){
        func(e1 as? E1, e2 as? E2, e3 as? E3)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, reified E2: E, reified E3: E, S: State>SubPub<S>.ges3(func: (List<E1>, List<E2>, List<E3>) -> Unit, nFunc: () -> Unit = {}, scopeTo: ScopeTo? = null){
    val e1 = getScopedEvents(E1::class, scopeTo)
    val e2 = getScopedEvents(E2::class, scopeTo)
    val e3 = getScopedEvents(E3::class, scopeTo)
    if (e1.isNotEmpty() || e2.isNotEmpty() || e3.isNotEmpty()){
        func(e1, e2, e3)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, reified E2: E, reified E3: E, S: State>SubPub<S>.gea3(func: (E1, E2, E3) -> Unit, nFunc: () -> Unit = {}){
    val e1 = getEvent(E1::class)
    val e2 = getEvent(E2::class)
    val e3 = getEvent(E2::class)
    if (e1 is E1 && e2 is E2 && e3 is E3){
        func(e1, e2, e3)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, reified E2: E, reified E3: E, S: State> SubPub<S>.geas3(func: (List<E1>, List<E2>, List<E3>) -> Unit, nFunc: () -> Unit = {}, scopeTo: ScopeTo? = null){
    val e1 = getScopedEvents(E1::class, scopeTo)
    val e2 = getScopedEvents(E2::class, scopeTo)
    val e3 = getScopedEvents(E3::class, scopeTo)
    if (e1.isNotEmpty() && e2.isNotEmpty() && e3.isNotEmpty()){
        func(e1, e2, e3)
    } else {
        nFunc()
    }
}

inline fun <reified E1: E, S: State>SubPub<S>.le(func: (E) -> Unit){
    if (lastEvent is E1) {
        func(lastEvent)
    }
}

inline fun <reified E1: E, reified E2: E, S: State>SubPub<S>.le2(func: (E1?, E2?) -> Unit){
    when(val le = lastEvent) {
        is E1 -> func(le, null)
        is E2 -> func(null, le)
    }
}

inline fun <reified E1: E, reified E2: E, reified E3: E, S: State>SubPub<S>.le3(func: (E1?, E2?, E3?) -> Unit){
    when (val le = lastEvent) {
        is E1 -> func(le, null, null)
        is E2 -> func(null, le, null)
        is E3 -> func(null, null, le)
    }
}