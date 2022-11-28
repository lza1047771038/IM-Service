package org.im.service.server.impl.handler

import org.im.service.Const
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.RequestHandler
import org.im.service.log.logDebug
import org.im.service.metadata.ClientRequest
import org.im.service.metadata.ServerResponse
import org.im.service.metadata.toUserSessionId
import java.nio.channels.SocketChannel

class MessageHandler(
    private val clientService: ClientService
): RequestHandler {
    override fun handle(socketChannel: SocketChannel, request: ClientRequest) {
        val toUserSessionId = request.toUserSessionId
        if (toUserSessionId.isEmpty()) {
            logDebug("send message with empty user id token")
            return
        }

        val isUserOnline = clientService.contains(toUserSessionId)
        if (!isUserOnline) {
            logDebug("user: $toUserSessionId is not online")
            return
        }

        val response = ServerResponse(method = Const.ResponseMethod.MESSAGE_TEXT, content = request.params)
        clientService.getChannel(toUserSessionId)?.writeResponse(response)
    }
}