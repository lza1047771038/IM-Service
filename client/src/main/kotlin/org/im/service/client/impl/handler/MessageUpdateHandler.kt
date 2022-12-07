package org.im.service.client.impl.handler

import org.im.service.Const
import org.im.service.client.impl.MessageImpl
import org.im.service.client.interfaces.Message
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.utils.notifyMessages
import org.im.service.interfaces.ResponseHandler
import org.im.service.log.logger
import org.im.service.utils.attachment
import org.im.service.utils.uuid
import org.json.JSONObject

/**
 * @author liuzhongao
 * @version 2022/12/7 15:47
 */
class MessageUpdateHandler(
    private val messageCallback: IMMessageCallback,
    private val attachmentParserFactory: Message.AttachmentParserFactory
): ResponseHandler {
    override fun handle(method: String, jsonObject: JSONObject) {
        logger.log("MessageUpdate", "update message: ${jsonObject.uuid}")

        val jsonAttachment = jsonObject.attachment
            .takeIf { it != null && it.isNotEmpty() }
            ?.let { kotlin.runCatching { JSONObject(it) }.getOrNull() }
        val parsedJSONObject = attachmentParserFactory.parse(jsonAttachment)
        val message = MessageImpl(jsonObject)
        message.attachment = parsedJSONObject
        messageCallback.notifyMessages(Const.Code.MESSAGE_UPDATE, message)
    }
}