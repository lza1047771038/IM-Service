package org.im.service.server.impl.channel

import org.im.service.interfaces.ClientService
import org.im.service.interfaces.GroupChannel
import org.im.service.interfaces.IEncryptor
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class GroupChannelImpl(
    override val sessionId: String,
    private val clientService: ClientService
): GroupChannel {

    private val joinedClients: MutableSet<String> = HashSet<String>()

    override fun joinGroup(sessionId: String) {
        if (!joinedClients.contains(sessionId)) {
            synchronized(joinedClients) {
                if (!joinedClients.contains(sessionId)) {
                    joinedClients.add(sessionId)
                }
            }
        }
    }

    override fun quitGroup(sessionId: String) {
        if (joinedClients.contains(sessionId)) {
            synchronized(joinedClients) {
                if (joinedClients.contains(sessionId)) {
                    joinedClients.remove(sessionId)
                }
            }
        }
    }

    override fun readRequest(byteBuffer: ByteBuffer, encryptor: IEncryptor): List<JSONObject?> = emptyList()

    override fun writeResponse(jsonObject: JSONObject) {
        val currentClients = HashSet<String>()
        synchronized(joinedClients) {
            currentClients.addAll(joinedClients)
        }
        currentClients.forEach { sessionId ->
            clientService.getChannel(sessionId)?.writeResponse(jsonObject)
        }
    }

    override fun hasLiveClients(): Boolean = true

    override fun containsChannel(socketChannel: SocketChannel): Boolean = false

    override fun addChannel(socketChannel: SocketChannel): Boolean = false

    override fun removeChannel(socketChannel: SocketChannel): Boolean = false

    override fun close() {

    }
}