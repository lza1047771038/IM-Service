package org.im.service.client.impl

import org.im.service.client.interfaces.callback.IMMessageCallback
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:22
 */
class SessionCallback: IMMessageCallback {

    private val callbacks: MutableList<IMMessageCallback> = ArrayList()

    fun addCallback(callback: IMMessageCallback) {
        if (callback is SessionCallback) {
            return
        }
        if (!callbacks.contains(callback)) {
            callbacks.add(callback)
        }
    }

    fun removeCallback(callback: IMMessageCallback) {
        callbacks.remove(callback)
    }

    override fun onNotify(code: Int, jsonObject: JSONObject?) {
        val iterator = callbacks.iterator()
        while (iterator.hasNext()) {
            iterator.next().onNotify(code, jsonObject)
        }
    }
}