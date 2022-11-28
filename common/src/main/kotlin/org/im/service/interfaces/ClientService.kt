package org.im.service.interfaces

import java.nio.channels.SocketChannel

interface ClientService {
    fun addClient(sessionId: String, socketChannel: SocketChannel)

    fun contains(key: String): Boolean

    fun contains(socketChannel: SocketChannel): Boolean

    fun getChannel(key: String): Channel?

    fun getChannel(socketChannel: SocketChannel): Channel?

    fun removeClient(sessionId: String)

    fun removeClient(socketChannel: SocketChannel)
}