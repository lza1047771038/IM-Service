package org.im.service.client.impl

import org.im.service.client.interfaces.SessionOperator
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.utils.IMUserInfo
import org.im.service.metadata.client.Message
import org.im.service.utils.sendRequest
import org.im.service.utils.toJSONObj
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * @author: liuzhongao
 * @date: 2022/11/29 15:06
 */
abstract class MsgSessionDelegate(
    private val imMessageCallback: IMMessageCallback
): SessionOperator {

    abstract val channel: SocketChannel

    abstract val writeBuffer: ByteBuffer

    // append self user info
    override fun sendMessage(message: Message) {
        message.fromUserId = IMUserInfo.selfUserId
        message.fromUser = IMUserInfo.selfAccount
        synchronized(writeBuffer) {
            channel.sendRequest(writeBuffer, message.toJSONObj())
        }
    }
}