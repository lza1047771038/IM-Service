package org.im.service.client.impl.handler

import org.im.service.Const
import org.im.service.client.impl.MessageHandler
import org.im.service.client.interfaces.callback.GlobalCallback
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.interfaces.ResponseHandler
import org.im.service.metadata.client.Message
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:23
 */
class ResponseHandlerWrapper(
    private val messageDecoder: Message.Factory,
    private val callback: IMMessageCallback
): ResponseHandler {

    private val innerHandler: MutableMap<String, ResponseHandler> = HashMap()

    init {
        innerHandler[Const.Method.USER_AUTHORIZATION] = UserAuthorizationResultHandler(callback)
        innerHandler[Const.Method.MESSAGE_TEXT] = MessageHandler(callback)
    }

    override fun handle(method: String, jsonObject: JSONObject) {
        val handler = innerHandler[method]
        handler?.handle(method, jsonObject)
    }
}