package org.im.service.interfaces

import java.nio.channels.SocketChannel

interface ClientInfo {
    fun addClient(token: String, socketChannel: SocketChannel)

    fun contains(key: String): Boolean

    fun contains(socketChannel: SocketChannel): Boolean

    fun getUserChannel(key: String): SocketChannel?

    fun removeClient(token: String)

    fun removeClient(socketChannel: SocketChannel)
}