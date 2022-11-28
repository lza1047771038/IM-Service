package org.im.service.client.interfaces

import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.Message

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:11
 */
interface MsgClient {

    fun init(imConfig: IMInitConfig)

    fun addGlobalCallback(callback: GlobalCallback)

    fun setOnReceiveResponseListener(onReceiveResponseListener: OnReceiveResponseListener)

    fun addFactory(factory: Message.Factory)

    fun authorization(): MsgAuthorization

}