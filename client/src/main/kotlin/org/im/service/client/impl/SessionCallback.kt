package org.im.service.client.impl

import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.metadata.NotifyWrapper
import java.util.LinkedList

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:22
 */
class SessionCallback: IMMessageCallback {

    private val callbacks: MutableList<IMMessageCallback> = LinkedList()

    fun addCallback(callback: IMMessageCallback) {
        if (callback is SessionCallback) {
            return
        }
        if (!callbacks.contains(callback)) {
            synchronized(callbacks) {
                callbacks.add(callback)
            }
        }
    }

    fun removeCallback(callback: IMMessageCallback) {
        if (!callbacks.contains(callback)) {
            return
        }
        synchronized(callbacks) {
            callbacks.remove(callback)
        }
    }

    override fun onNotify(wrapper: NotifyWrapper?) {
        val iterator = callbacks.iterator()
        while (iterator.hasNext()) {
            iterator.next().onNotify(wrapper)
        }
    }
}