package org.im.service.server.controller.dispatcher

import org.im.service.interfaces.IEncryptor
import org.im.service.interfaces.RequestHandler
import org.im.service.message.queue.interfaces.MessageQueue
import org.im.service.message.queue.interfaces.execute
import org.im.service.utils.method
import org.im.service.utils.readJSONFromRemote
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * @author: liuzhongao
 * @date: 2022/11/28 12:47
 */
class SocketChannelDispatcherImpl(
    private val messageQueue: MessageQueue,
    private val requestHandler: RequestHandler
) : AbsSocketChannelDispatcher() {

    var encryptor: IEncryptor? = null

    override fun onAcceptReadable(byteBuffer: ByteBuffer, socketChannel: SocketChannel) {
        messageQueue.execute {
            val clientMessages = socketChannel.readJSONFromRemote(byteBuffer, encryptor)
            clientMessages.forEach { message ->
                if (message != null) {
                    val method = message.method
                    requestHandler.handle(method, message, socketChannel)
                }
            }
        }
    }
}