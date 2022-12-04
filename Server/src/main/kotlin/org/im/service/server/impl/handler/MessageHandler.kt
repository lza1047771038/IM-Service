package org.im.service.server.impl.handler

import org.im.service.client.metadata.SessionType
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.GroupService
import org.im.service.interfaces.Logger
import org.im.service.interfaces.RequestHandler
import org.im.service.log.logger
import org.im.service.utils.*
import org.json.JSONObject
import java.nio.channels.SocketChannel

class MessageHandler(
    private val clientService: ClientService,
    private val groupService: GroupService
): RequestHandler {

    override fun handle(method: String, jsonObject: JSONObject, socketChannel: SocketChannel) {
        val toSessionId = jsonObject.toSessionId
        if (toSessionId.isEmpty()) {
            logger.log("MessageConsumer", "send message with empty user id token")
            return
        }

        logger.log("MessageConsumer", "received message from: ${jsonObject.fromUserId}, content: ${jsonObject.textContent}")

        when (jsonObject.sessionType) {
            SessionType.P2P -> handleP2PMessages(toSessionId, jsonObject)
            SessionType.Group -> handleGroupMessages(toSessionId, jsonObject)
            else -> {}
        }
    }

    private fun handleP2PMessages(toSessionId: String, jsonObject: JSONObject) {
        val isUserOnline = clientService.contains(toSessionId)
        if (!isUserOnline) {
            logger.log("MessageConsumer", "user: $toSessionId is not online", Logger.Type.Error)
            return
        }
        clientService.getChannel(toSessionId)?.writeResponse(jsonObject)
    }

    private fun handleGroupMessages(toSessionId: String, jsonObject: JSONObject) {
        groupService.getGroupChannel(toSessionId)?.writeResponse(jsonObject)
    }
}