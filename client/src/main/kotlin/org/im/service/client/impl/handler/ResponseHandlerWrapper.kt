package org.im.service.client.impl.handler

import org.im.service.Const
import org.im.service.client.interfaces.GlobalCallback
import org.im.service.interfaces.RequestHandler
import org.im.service.interfaces.ResponseHandler
import org.im.service.log.logDebug
import org.im.service.metadata.ClientRequest
import org.im.service.metadata.client.Message
import org.im.service.metadata.isAuthorizationRequest
import org.im.service.metadata.toUserSessionId
import org.json.JSONObject
import java.nio.channels.SocketChannel

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:23
 */
class ResponseHandlerWrapper(
    private val messageDecoder: Message.Factory,
    private val callback: GlobalCallback
): ResponseHandler {

    private val innerHandler: MutableMap<String, ResponseHandler> = HashMap()

    init {
        innerHandler[Const.ResponseMethod.USER_AUTHORIZATION] = UserAuthorizationResultHandler(callback)
    }

    override fun handle(method: String, jsonObject: JSONObject) {
        val handler = innerHandler[method]
        handler?.handle(method, jsonObject)
    }
}