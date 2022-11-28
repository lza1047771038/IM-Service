package org.im.service.server.impl.channel

import org.im.service.interfaces.Channel
import org.im.service.interfaces.IEncryptor
import org.im.service.metadata.ClientRequest
import org.im.service.metadata.ServerResponse
import org.im.service.utils.closeSilently
import org.im.service.utils.readRequest
import org.im.service.utils.responseTo
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.LinkedList

/**
 * @author: liuzhongao
 * @date: 2022/11/28 11:49
 */
class ChannelImpl(
    socketChannel: SocketChannel
) : Channel {

    private val clientSocketChannel: LinkedList<SocketChannel> = LinkedList()

    init {
        clientSocketChannel.add(socketChannel)
    }

    override fun readRequest(byteBuffer: ByteBuffer, encryptor: IEncryptor): List<ClientRequest?> {
        if (clientSocketChannel.isEmpty()) {
            return emptyList()
        }
        val retList = LinkedList<ClientRequest?>()
        synchronized(clientSocketChannel) {
            for (socketChannel in clientSocketChannel) {
                val clientRequests = socketChannel.readRequest(byteBuffer, encryptor)
                retList.addAll(clientRequests)
            }
        }
        return retList
    }

    override fun writeResponse(response: ServerResponse) {
        clientSocketChannel.forEach { socketChannel ->
            socketChannel.responseTo(response)
        }
    }

    override fun hasLiveClients(): Boolean {
        return clientSocketChannel.find { it.isConnected && it.isOpen } != null
    }

    override fun containsChannel(socketChannel: SocketChannel): Boolean {
        return clientSocketChannel.find { it.remoteAddress == socketChannel.remoteAddress } != null && socketChannel.isConnected
    }

    override fun addChannel(socketChannel: SocketChannel): Boolean {
        var ret = false
        synchronized(clientSocketChannel) {
            if (!containsChannel(socketChannel)) {
                ret = clientSocketChannel.add(socketChannel)
            }
        }
        return ret
    }

    override fun removeChannel(socketChannel: SocketChannel): Boolean {
        var ret = false
        synchronized(clientSocketChannel) {
            if (!containsChannel(socketChannel)) {
                ret = clientSocketChannel.removeAll { it.remoteAddress == socketChannel.remoteAddress }
            }
        }
        return ret
    }

    override fun close() {
        synchronized(clientSocketChannel) {
            val iterator = clientSocketChannel.iterator()
            while (iterator.hasNext()) {
                val channel = iterator.next()
                channel.closeSilently()
                iterator.remove()
            }
        }
    }
}