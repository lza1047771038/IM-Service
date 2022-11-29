package org.im.service.server.impl.handler

import org.im.service.interfaces.ClientService
import org.im.service.interfaces.RequestHandler
import org.im.service.log.logDebug
import org.im.service.utils.toUserSessionId
import org.json.JSONObject
import java.nio.channels.SocketChannel

class MessageHandler(
    private val clientService: ClientService
): RequestHandler {
    override fun handle(method: String, jsonObject: JSONObject, socketChannel: SocketChannel) {
        val toUserSessionId = jsonObject.toUserSessionId
        if (toUserSessionId.isEmpty()) {
            logDebug("send message with empty user id token")
            return
        }

        val isUserOnline = clientService.contains(toUserSessionId)
        if (!isUserOnline) {
            logDebug("user: $toUserSessionId is not online")
            return
        }

        clientService.getChannel(toUserSessionId)?.writeResponse(jsonObject)
    }
}