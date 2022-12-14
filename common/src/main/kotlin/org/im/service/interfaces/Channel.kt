package org.im.service.interfaces

import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

/**
 * @author: liuzhongao
 * @date: 2022/11/28 10:22
 */
interface Channel {
    val sessionId: String

    fun readRequest(byteBuffer: ByteBuffer, encryptor: IEncryptor): List<JSONObject?>
    fun writeResponse(jsonObject: JSONObject)
    fun hasLiveClients(): Boolean
    fun containsChannel(socketChannel: SocketChannel): Boolean
    fun addChannel(socketChannel: SocketChannel): Boolean
    fun removeChannel(socketChannel: SocketChannel): Boolean
    fun close()
}