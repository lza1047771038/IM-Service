package org.im.service.client.impl

import org.im.service.Const
import org.im.service.client.interfaces.SessionOperator
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.utils.IMUserInfo
import org.im.service.client.utils.notifyMessages
import org.im.service.log.logger
import org.im.service.client.interfaces.Message
import org.im.service.client.interfaces.MessageProgressCallback
import org.im.service.utils.sendRequest
import org.im.service.utils.toJSONObj
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * @author: liuzhongao
 * @date: 2022/11/29 15:06
 */
abstract class MsgSessionDelegate(
    private val imMessageCallback: IMMessageCallback,
): SessionOperator {

    abstract val channel: SocketChannel?

    abstract val writeBuffer: ByteBuffer

    // append self user info
    override fun sendMessage(message: Message) {
        sendMessage(message, null)
    }

    override fun sendMessage(message: Message, messageProgressCallback: MessageProgressCallback?) {
        message.fromUserId = IMUserInfo.selfUserId
        message.fromUser = IMUserInfo.selfAccount

        val socketChannel = this.channel
        if (socketChannel == null) {
            logger.log("Warning", "socketChannel may be closed or disconnected, current message: ${message.uuid} will be written into database storage")
            imMessageCallback.notifyMessages(Const.Code.MESSAGE_SEND_ERROR, message)
            return
        }

        synchronized(writeBuffer) {
            socketChannel.sendRequest(
                buffer = writeBuffer,
                request = message.toJSONObj(),
                sendSuccess = {
                    messageProgressCallback?.onSuccess(message)
                    imMessageCallback.notifyMessages(Const.Code.MESSAGE_SEND_SUCCESS, message)
                },
                onError = {
                    messageProgressCallback?.onFailed(message, Exception(it))
                    imMessageCallback.notifyMessages(Const.Code.MESSAGE_SEND_ERROR, message)
                }
            )
        }
    }

    override fun deleteMessage(message: Message) {

    }

}