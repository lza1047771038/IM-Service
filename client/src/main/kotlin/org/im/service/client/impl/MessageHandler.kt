package org.im.service.client.impl

import org.im.service.Const
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.interfaces.ResponseHandler
import org.im.service.log.logger
import org.im.service.utils.fromUser
import org.im.service.utils.fromUserId
import org.im.service.utils.textContent
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:40
 */
class MessageHandler(
    private val messageCallback: IMMessageCallback
): ResponseHandler {
    override fun handle(method: String, jsonObject: JSONObject) {
        logger.log("MessageReceiver", "receive message from: ${jsonObject.fromUserId}, content: ${jsonObject.textContent}")
        messageCallback.onNotify(Const.Code.MESSAGE_RECEIVED, jsonObject)
    }
}