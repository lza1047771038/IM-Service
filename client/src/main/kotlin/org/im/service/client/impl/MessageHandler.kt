package org.im.service.client.impl

import org.im.service.Const
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.interfaces.ResponseHandler
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:40
 */
class MessageHandler(
    private val messageCallback: IMMessageCallback
): ResponseHandler {
    override fun handle(method: String, jsonObject: JSONObject) {
        messageCallback.onNotify(Const.Code.MESSAGE_RECEIVED, jsonObject)
    }
}