package org.im.service.server.impl.channel

import org.im.service.interfaces.Channel
import org.im.service.interfaces.IEncryptor
import org.im.service.utils.DisconnectedCallback
import org.im.service.utils.closeSilently
import org.im.service.utils.readJSONFromRemote
import org.im.service.utils.responseTo
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.*

/**
 * @author: liuzhongao
 * @date: 2022/11/28 11:49
 */
class ChannelImpl(
    override val sessionId: String,
    socketChannel: SocketChannel
) : Channel {

    private val clientSocketChannel: LinkedList<SocketChannel> = LinkedList()
    private val disconnectedCallback: DisconnectedCallback = { removeChannel(this) }

    init {
        clientSocketChannel.add(socketChannel)
    }

    override fun readRequest(byteBuffer: ByteBuffer, encryptor: IEncryptor): List<JSONObject?> {
        if (clientSocketChannel.isEmpty()) {
            return emptyList()
        }
        val retList = LinkedList<JSONObject?>()
        synchronized(clientSocketChannel) {
            for (socketChannel in clientSocketChannel) {
                if (!socketChannel.isOpen || !socketChannel.isConnected) {
                    continue
                }
                val clientRequests = socketChannel.readJSONFromRemote(byteBuffer, encryptor, disconnectedCallback)
                retList.addAll(clientRequests)
            }
        }
        return retList
    }

    override fun writeResponse(jsonObject: JSONObject) {
        clientSocketChannel.forEach { socketChannel ->
            if (!socketChannel.isOpen || !socketChannel.isConnected) {
                return@forEach
            }
            socketChannel.responseTo(jsonObject, disconnectedCallback = {
                closeSilently()
            })
        }
    }

    override fun hasLiveClients(): Boolean {
        return clientSocketChannel.find { it.isOpen && it.isConnected } != null
    }

    override fun containsChannel(socketChannel: SocketChannel): Boolean {
        return socketChannel.isOpen && clientSocketChannel.find { it.isOpen && it.remoteAddress == socketChannel.remoteAddress } != null
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

    fun cleanRemoteSocket() {
        synchronized(clientSocketChannel) {
            val iterator = clientSocketChannel.iterator()
            while (iterator.hasNext()) {
                val channel = iterator.next()
                if (!channel.isOpen || !channel.isConnected) {
                    channel.closeSilently()
                    iterator.remove()
                }
            }
        }
    }
}