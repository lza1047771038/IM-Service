package org.im.service.server.impl

import org.im.service.interfaces.ClientInfo
import org.im.service.utils.closeSilently
import java.nio.channels.SocketChannel
import java.util.concurrent.ConcurrentHashMap

class ClientServiceWrapper: ClientInfo {

    private val clientTokenMap: MutableMap<String, SocketChannel> = ConcurrentHashMap()

    override fun addClient(token: String, socketChannel: SocketChannel) {
        if (!clientTokenMap.containsKey(token)) {
            clientTokenMap[token] = socketChannel
        } else {
            removeClient(token)
            clientTokenMap[token] = socketChannel
        }
    }

    override fun contains(key: String): Boolean {
        return clientTokenMap.containsKey(key)
    }

    override fun contains(socketChannel: SocketChannel): Boolean {
        var contains = false
        for (entry in clientTokenMap) {
            if (entry.value == socketChannel) {
                contains = true
            }
        }
        return contains
    }

    override fun getUserChannel(key: String): SocketChannel? {
        return clientTokenMap[key]
    }

    override fun removeClient(token: String) {
        val exist = clientTokenMap[token]
        exist?.closeSilently()
    }

    override fun removeClient(socketChannel: SocketChannel) {
        val mapIterator = clientTokenMap.iterator()
        while (mapIterator.hasNext()) {
            val entry = mapIterator.next()
            if (entry.value == socketChannel) {
                removeClient(entry.key)
                break
            }
        }
    }
}