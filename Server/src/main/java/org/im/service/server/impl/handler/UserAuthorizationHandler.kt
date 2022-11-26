package org.im.service.server.impl.handler

import okio.ByteString.Companion.decodeBase64
import org.im.service.Const
import org.im.service.interfaces.ClientInfo
import org.im.service.interfaces.RequestHandler
import org.im.service.metadata.*
import org.im.service.utils.responseTo
import java.nio.channels.SocketChannel

class UserAuthorizationHandler(
    private val clientInfo: ClientInfo
) : RequestHandler {
    override fun handle(socketChannel: SocketChannel, request: ClientRequest) {
        val clientToken = request.clientToken
        if (clientToken.isNotEmpty()) {
            clientInfo.addClient(clientToken, socketChannel)
        } else {
            val clientUserId = request.clientUserId
            if (clientUserId.isNotEmpty()) {
                val generatedClientToken = clientUserId.generateUserToken()
                clientInfo.addClient(generatedClientToken, socketChannel)
                val serverResponse = userAuthorizationResponse(generatedClientToken)
                socketChannel.responseTo(serverResponse)
            }
        }
    }

    private fun String.generateUserToken(): String {
        return this.decodeBase64()?.base64() ?: ""
    }

    private fun userAuthorizationResponse(generatedToken: String): ServerResponse {
        return ServerResponse(method = Const.ResponseMethod.USER_AUTHORIZATION).apply {
            this.clientToken = generatedToken
        }
    }

}
