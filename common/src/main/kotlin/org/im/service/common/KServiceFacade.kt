package org.im.service.common


object KServiceFacade {
    operator fun <T, R: T> set(key: Class<T>, value: R) = ServiceFacade.put(key, value)

    operator fun <T> get(key: Class<T>) = ServiceFacade.get(key)
}