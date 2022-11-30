package org.im.service.server.impl

import org.im.service.interfaces.Channel
import org.im.service.interfaces.ClientService
import org.im.service.server.impl.channel.ChannelImpl
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

class ClientServiceWrapper: ClientService {

    private val clientSessionMap: MutableMap<String, ChannelImpl> = ConcurrentHashMap()

    override fun addClient(sessionId: String, socketChannel: SocketChannel) {
        if (!clientSessionMap.containsKey(sessionId)) {
            clientSessionMap[sessionId] = ChannelImpl(socketChannel)
        } else {
            val channel = clientSessionMap[sessionId]
            channel?.addChannel(socketChannel)
        }
    }

    override fun contains(key: String): Boolean {
        val value = clientSessionMap[key]
        return value != null && value.hasLiveClients()
    }

    override fun contains(socketChannel: SocketChannel): Boolean {
        var contains = false
        for (entry in clientSessionMap) {
            if (entry.value.containsChannel(socketChannel) ) {
                contains = true
            }
        }
        return contains
    }

    override fun getChannel(key: String): Channel? {
        return clientSessionMap[key]
    }

    override fun getChannel(socketChannel: SocketChannel): Channel? {
        return clientSessionMap.values.find { it.containsChannel(socketChannel) }
    }

    override fun removeClient(sessionId: String) {
        val exist = clientSessionMap.remove(sessionId)
        exist?.close()
    }

    override fun removeClient(socketChannel: SocketChannel) {
        val mapIterator = clientSessionMap.iterator()
        while (mapIterator.hasNext()) {
            val entry = mapIterator.next()
            val channel = entry.value
            if (channel.containsChannel(socketChannel)) {
                channel.removeChannel(socketChannel)
                break
            }
        }
    }
}