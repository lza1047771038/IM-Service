package org.im.service.server.impl.handler

import org.im.service.Const
import org.im.service.interfaces.ClientInfo
import org.im.service.interfaces.RequestHandler
import org.im.service.metadata.ClientRequest
import org.im.service.metadata.clientToken
import org.im.service.metadata.isAuthorizationRequest
import java.nio.channels.SocketChannel

class RequestHandlerWrapper(
    private val clientInfo: ClientInfo
): RequestHandler {
    private val innerHandler: MutableMap<String, RequestHandler> = HashMap()

    init {
        innerHandler[Const.RequestMethod.USER_AUTHORIZATION] = UserAuthorizationHandler(clientInfo)
        innerHandler[Const.RequestMethod.MESSAGE_TEXT] = MessageHandler(clientInfo)
    }

    override fun handle(socketChannel: SocketChannel, request: ClientRequest) {
        val clientToken = request.clientToken
        val isLogin = request.isAuthorizationRequest || (clientToken.isNotEmpty())
        if (!isLogin) {
            println("client is not login with request: $request")
            return
        }
        val handler = innerHandler[request.method]
        if (handler == null) {
            println("no handler for method: ${request.method}")
            return
        }
        handler.handle(socketChannel, request)
    }

}