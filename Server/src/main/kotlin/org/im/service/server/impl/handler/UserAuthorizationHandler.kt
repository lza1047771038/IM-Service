package org.im.service.server.impl.handler

import okio.ByteString.Companion.encodeUtf8
import org.im.service.Const
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.RequestHandler
import org.im.service.log.logger
import org.im.service.utils.*
import org.json.JSONObject
import java.nio.channels.SocketChannel

class UserAuthorizationHandler(
    private val clientService: ClientService
) : RequestHandler {

    override fun handle(method: String, jsonObject: JSONObject, socketChannel: SocketChannel) {
        logger.log("UserAuthorization", "accept login request from: ${jsonObject.fromUserId}, sessionId: ${jsonObject.fromUserSessionId}")
        var clientSessionId = jsonObject.fromUserSessionId
        if (clientSessionId.isNotEmpty()) {
            clientService.addClient(clientSessionId, socketChannel)
        } else {
            val clientUserId = jsonObject.fromUserId
            if (clientUserId.isNotEmpty()) {
                clientSessionId = clientUserId.generateUserToken()
                clientService.addClient(clientSessionId, socketChannel)
            } else {
                logger.log("UserAuthorization", "connect with empty sessionId and userId")
                return
            }
        }

        val serverResponseMessage = createEmptyMessage()
        serverResponseMessage.remoteExtensions = mutableMapOf(Const.Param.PARAM_USER_SESSION_ID to clientSessionId)

        val serverResponse = serverResponseMessage.toJSONObj(Const.Method.USER_AUTHORIZATION)

        clientService.getChannel(clientSessionId)?.writeResponse(serverResponse)
    }

    private fun String.generateUserToken(): String {
        return this.encodeUtf8().base64()
    }

}
