package org.im.service.client.impl

import org.im.service.log.logDebug
import org.im.service.metadata.client.Message
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午9:41
 */
class MessageDecodeFactory: Message.DecodeFactory {

    fun addDecodeFactory(decodeFactory: Message.DecodeFactory) {

    }

    override fun decode(jsonObject: JSONObject): Message? {
        logDebug("receive message: $jsonObject")
        return null
    }
}