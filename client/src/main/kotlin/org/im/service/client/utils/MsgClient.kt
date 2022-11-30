package org.im.service.client.utils

import org.im.service.Const
import org.im.service.client.interfaces.MsgClient
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:43
 */

fun MsgClient.onConnectionEstablished(invocation: () -> Unit) {
    addMessageCallback(object: IMMessageCallback {
        override fun onNotify(code: Int, jsonObject: JSONObject?) {
            if (code == Const.Code.CONNECTION_ESTABLISHED) {
                invocation()
            }
        }
    })
}

fun MsgClient.onDisconnected(invocation: () -> Unit) {
    addMessageCallback(object: IMMessageCallback {
        override fun onNotify(code: Int, jsonObject: JSONObject?) {
            if (code == Const.Code.SESSION_DISCONNECTED) {
                invocation()
            }
        }
    })
}