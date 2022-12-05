package org.im.service.client.impl.handler

import org.im.service.Const
import org.im.service.client.impl.MessageImpl
import org.im.service.client.interfaces.Message
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.utils.notifyMessages
import org.im.service.interfaces.ResponseHandler
import org.im.service.log.logger
import org.im.service.utils.attachment
import org.im.service.utils.fromUserId
import org.im.service.utils.textContent
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:40
 */
class MessageHandler(
    private val messageCallback: IMMessageCallback,
    private val attachmentParserFactory: Message.AttachmentParserFactory
): ResponseHandler {
    override fun handle(method: String, jsonObject: JSONObject) {
        logger.log("MessageReceiver", "receive message from: ${jsonObject.fromUserId}, content: ${jsonObject.textContent}")

        val jsonAttachment = jsonObject.attachment
            .takeIf { it != null && it.isNotEmpty() }
            ?.let { kotlin.runCatching { JSONObject(it) }.getOrNull() }
        val parsedJSONObject = attachmentParserFactory.parse(jsonAttachment)
        val message = MessageImpl(jsonObject)
        message.attachment = parsedJSONObject
        messageCallback.notifyMessages(Const.Code.MESSAGE_RECEIVED, message)
    }
}