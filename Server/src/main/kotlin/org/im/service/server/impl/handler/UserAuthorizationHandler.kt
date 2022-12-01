package org.im.service.server.impl.handler

import okio.ByteString.Companion.encodeUtf8
import org.im.service.Const
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.RequestHandler
import org.im.service.metadata.client.MsgAccount
import org.im.service.utils.*
import org.json.JSONObject
import java.nio.channels.SocketChannel

class UserAuthorizationHandler(
    private val clientService: ClientService
) : RequestHandler {

    override fun handle(method: String, jsonObject: JSONObject, socketChannel: SocketChannel) {
        val clientSessionId = jsonObject.fromUserSessionId
        if (clientSessionId.isNotEmpty()) {
            clientService.addClient(clientSessionId, socketChannel)
        } else {
            val clientUserId = jsonObject.fromUserId
            if (clientUserId.isNotEmpty()) {
                handleFirstAuthorization(clientUserId, socketChannel)
            } else {

            }
        }
    }

    private fun handleFirstAuthorization(clientUserId: String, socketChannel: SocketChannel) {
        val generatedClientSessionId = clientUserId.generateUserToken()
        val serverResponseMessage = createEmptyMessage()

        serverResponseMessage.fromUser = MsgAccount(generatedClientSessionId)
        serverResponseMessage.toUser = MsgAccount(generatedClientSessionId)

        val serverResponse = serverResponseMessage.toJSONObj(Const.Method.USER_AUTHORIZATION)

        clientService.addClient(generatedClientSessionId, socketChannel)
        clientService.getChannel(generatedClientSessionId)?.writeResponse(serverResponse)
    }

    private fun String.generateUserToken(): String {
        return this.encodeUtf8().base64()
    }

}
