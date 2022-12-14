package org.im.service.client.impl.handler

import org.im.service.Const
import org.im.service.client.interfaces.Message
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.interfaces.ResponseHandler
import org.im.service.log.logDebug
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:23
 */
internal class ResponseHandlerWrapper(
    callback: IMMessageCallback,
    attachmentParserFactory: Message.AttachmentParserFactory
): ResponseHandler {

    private val innerHandler: MutableMap<String, ResponseHandler> = HashMap()

    init {
        innerHandler[Const.Method.USER_AUTHORIZATION] = UserAuthorizationResultHandler(callback)
        innerHandler[Const.Method.MESSAGE_TEXT] = MessageHandler(callback, attachmentParserFactory)
        innerHandler[Const.Method.MESSAGE_STATE_UPDATE] = MessageUpdateHandler(callback, attachmentParserFactory)
    }

    fun addHandler(method: String, handler: ResponseHandler) {
        if (innerHandler.containsKey(method)) {
            val existHandler = innerHandler[method]
            if (existHandler == null) {
                innerHandler[method] = handler
            } else {
                logDebug("duplicated handler with the method: ${method}, handler: ${existHandler.javaClass.name}, current setter is skipped")
            }
        } else {
            innerHandler[method] = handler
        }
    }

    fun removeHandler(method: String) {
        innerHandler.remove(method)
    }

    override fun handle(method: String, jsonObject: JSONObject) {
        val handler = innerHandler[method]
        handler?.handle(method, jsonObject)
    }
}