package com.doyoumate.common.logging

import org.reactivestreams.Subscription
import org.slf4j.MDC
import reactor.core.CoreSubscriber
import reactor.util.context.Context

class MdcContextLifter<T>(private val coreSubscriber: CoreSubscriber<T>) : CoreSubscriber<T> {
    override fun onSubscribe(subscription: Subscription) {
        coreSubscriber.onSubscribe(subscription)
    }

    override fun onNext(t: T) {
        currentContext().synchronizeMdc()
        coreSubscriber.onNext(t)
    }

    override fun onError(throwable: Throwable) {
        coreSubscriber.onError(throwable)
    }

    override fun onComplete() {
        coreSubscriber.onComplete()
    }

    override fun currentContext(): Context = coreSubscriber.currentContext()

    private fun Context.synchronizeMdc() {
        getOrEmpty<String>("traceId")
            .ifPresent { MDC.put("traceId", it) }
    }
}
