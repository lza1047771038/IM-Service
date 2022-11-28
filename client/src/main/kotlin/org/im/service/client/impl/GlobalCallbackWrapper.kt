package org.im.service.client.impl

import org.im.service.client.interfaces.GlobalCallback
import java.util.LinkedList

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:43
 */
class GlobalCallbackWrapper: GlobalCallback {
    private val callbackList: MutableList<GlobalCallback> = LinkedList()

    fun addCallback(callback: GlobalCallback) {
        callbackList.add(callback)
    }

    override fun onConnectionSuccess() {
        callbackList.forEach { it.onConnectionSuccess() }
    }

    override fun onServiceLoginSuccess(sessionId: String) {
        callbackList.forEach { it.onServiceLoginSuccess(sessionId) }
    }
}