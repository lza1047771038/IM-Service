package org.im.service.server.impl.handler

import org.im.service.Const
import org.im.service.interfaces.ClientInfo
import org.im.service.interfaces.RequestHandler
import org.im.service.metadata.ClientRequest
import org.im.service.metadata.ServerResponse
import org.im.service.metadata.toUserIdToken
import org.im.service.utils.responseTo
import java.nio.channels.SocketChannel

class MessageHandler(
    private val clientInfo: ClientInfo
): RequestHandler {
    override fun handle(socketChannel: SocketChannel, request: ClientRequest) {
        val toUserIdToken = request.toUserIdToken
        if (toUserIdToken.isEmpty()) {
            println("send message with empty user id token")
            return
        }

        val isUserOnline = clientInfo.contains(toUserIdToken)
        if (!isUserOnline) {
            println("user: $toUserIdToken is not online")
            return
        }

        val response = ServerResponse(method = Const.ResponseMethod.MESSAGE_TEXT, data = request.params)
        clientInfo.getUserChannel(toUserIdToken)?.responseTo(response)
    }
}