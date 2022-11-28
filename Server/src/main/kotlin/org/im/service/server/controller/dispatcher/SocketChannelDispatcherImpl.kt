package org.im.service.server.controller.dispatcher

import org.im.service.interfaces.IEncryptor
import org.im.service.interfaces.RequestHandler
import org.im.service.message.queue.interfaces.MessageQueue
import org.im.service.message.queue.interfaces.execute
import org.im.service.utils.readRequest
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
            val clientRequests = socketChannel.readRequest(byteBuffer, encryptor)
            clientRequests.forEach { clientRequest ->
                if (clientRequest != null) {
                    requestHandler.handle(socketChannel, clientRequest)
                }
            }
        }
    }
}