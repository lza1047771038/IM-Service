package org.im.service.server.impl.handler

import org.im.service.Const
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.RequestHandler
import org.im.service.log.logDebug
import org.im.service.utils.fromUserSessionId
import org.im.service.utils.isAuthorizationRequest
import org.im.service.utils.method
import org.json.JSONObject
import java.nio.channels.SocketChannel

class RequestHandlerWrapper(
    private val clientService: ClientService
): RequestHandler {
    private val innerHandler: MutableMap<String, RequestHandler> = HashMap()

    init {
        innerHandler[Const.Method.USER_AUTHORIZATION] = UserAuthorizationHandler(clientService)
        innerHandler[Const.Method.MESSAGE_TEXT] = MessageHandler(clientService)
    }

    override fun handle(method: String, jsonObject: JSONObject, socketChannel: SocketChannel) {
        val clientSessionId = jsonObject.fromUserSessionId
        val isLogin = jsonObject.isAuthorizationRequest || (clientSessionId.isNotEmpty())
        if (!isLogin) {
            logDebug("client is not login with request: $jsonObject")
            return
        }
        val handler = innerHandler[method]
        if (handler == null) {
            logDebug("no handler for method: ${jsonObject.method}")
            return
        }
        handler.handle(method, jsonObject, socketChannel)
    }

}