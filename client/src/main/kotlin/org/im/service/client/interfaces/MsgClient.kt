package org.im.service.client.interfaces

import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.Message

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:11
 */
interface MsgClient {

    fun init(imConfig: IMInitConfig)

    fun addMessageCallback(callback: IMMessageCallback)

    fun removeMessageCallback(callback: IMMessageCallback)

    fun setOnReceiveResponseListener(onReceiveResponseListener: OnReceiveResponseListener)

    fun addFactory(factory: Message.Factory)

    fun authorization(): MsgAuthorization

    fun msgOperator(): MsgOperator

    fun disconnect()
}