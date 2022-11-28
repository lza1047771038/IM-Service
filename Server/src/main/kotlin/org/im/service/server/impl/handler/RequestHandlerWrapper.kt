package org.im.service.server.impl.handler

import org.im.service.Const
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.RequestHandler
import org.im.service.log.logDebug
import org.im.service.metadata.ClientRequest
import org.im.service.metadata.isAuthorizationRequest
import org.im.service.metadata.toUserSessionId
import java.nio.channels.SocketChannel

class RequestHandlerWrapper(
    private val clientService: ClientService
): RequestHandler {
    private val innerHandler: MutableMap<String, RequestHandler> = HashMap()

    init {
        innerHandler[Const.RequestMethod.USER_AUTHORIZATION] = UserAuthorizationHandler(clientService)
        innerHandler[Const.RequestMethod.MESSAGE_TEXT] = MessageHandler(clientService)
    }

    override fun handle(socketChannel: SocketChannel, request: ClientRequest) {
        val clientSessionId = request.toUserSessionId
        val isLogin = request.isAuthorizationRequest || (clientSessionId.isNotEmpty())
        if (!isLogin) {
            logDebug("client is not login with request: $request")
            return
        }
        val handler = innerHandler[request.method]
        if (handler == null) {
            logDebug("no handler for method: ${request.method}")
            return
        }
        handler.handle(socketChannel, request)
    }

}