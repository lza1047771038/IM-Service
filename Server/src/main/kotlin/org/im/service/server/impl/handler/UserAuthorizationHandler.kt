package org.im.service.server.impl.handler

import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.encodeUtf8
import org.im.service.Const
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.RequestHandler
import org.im.service.metadata.*
import java.nio.channels.SocketChannel

class UserAuthorizationHandler(
    private val clientService: ClientService
) : RequestHandler {
    override fun handle(socketChannel: SocketChannel, request: ClientRequest) {
        val clientSessionId = request.clientSessionId
        if (clientSessionId.isNotEmpty()) {
            clientService.addClient(clientSessionId, socketChannel)
        } else {
            val clientUserId = request.clientUserId
            if (clientUserId.isNotEmpty()) {
                val generatedClientSessionId = clientUserId.generateUserToken()
                val serverResponse = userAuthorizationResponse(generatedClientSessionId)
                clientService.addClient(generatedClientSessionId, socketChannel)
                clientService.getChannel(generatedClientSessionId)?.writeResponse(serverResponse)
            }
        }
    }

    private fun String.generateUserToken(): String {
        return this.encodeUtf8().base64()
    }

    private fun userAuthorizationResponse(generatedSessionId: String): ServerResponse {
        return ServerResponse(method = Const.ResponseMethod.USER_AUTHORIZATION).apply {
            this.sessionId = generatedSessionId
        }
    }

}
