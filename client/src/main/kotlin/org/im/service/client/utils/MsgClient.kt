package org.im.service.client.utils

import org.im.service.Const
import org.im.service.client.interfaces.MsgClient
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.metadata.NotifyWrapper

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:43
 */

fun MsgClient.onConnectionEstablished(invocation: () -> Unit) = modification {
    addIMMessageCallback(object: IMMessageCallback {
        override fun onNotify(wrapper: NotifyWrapper?) {
            if (wrapper?.code == Const.Code.CONNECTION_ESTABLISHED) {
                removeIMMessageCallback(this)
                invocation()
            }
        }
    })
}

fun MsgClient.onDisconnected(invocation: () -> Unit) = modification {
    addIMMessageCallback(object: IMMessageCallback {
        override fun onNotify(wrapper: NotifyWrapper?) {
            if (wrapper?.code == Const.Code.SESSION_DISCONNECTED) {
                removeIMMessageCallback(this)
                invocation()
            }
        }
    })
}