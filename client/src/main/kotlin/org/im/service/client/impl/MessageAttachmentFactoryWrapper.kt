package org.im.service.client.impl

import org.im.service.client.interfaces.Attachment
import org.im.service.client.interfaces.Message
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/12/2 17:47
 */
internal class MessageAttachmentFactoryWrapper: Message.AttachmentParserFactory {

    fun addFactory(factory: Message.AttachmentParserFactory) {

    }

    override fun parse(jsonObject: JSONObject?): Attachment? {
        return null
    }
}