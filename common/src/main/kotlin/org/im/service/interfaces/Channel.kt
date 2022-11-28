package org.im.service.interfaces

import org.im.service.metadata.ClientRequest
import org.im.service.metadata.ServerResponse
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * @author: liuzhongao
 * @date: 2022/11/28 10:22
 */
interface Channel {
    fun readRequest(byteBuffer: ByteBuffer, encryptor: IEncryptor): List<ClientRequest?>
    fun writeResponse(response: ServerResponse)

    fun hasLiveClients(): Boolean

    fun containsChannel(socketChannel: SocketChannel): Boolean
    fun addChannel(socketChannel: SocketChannel): Boolean
    fun removeChannel(socketChannel: SocketChannel): Boolean
    fun close()
}