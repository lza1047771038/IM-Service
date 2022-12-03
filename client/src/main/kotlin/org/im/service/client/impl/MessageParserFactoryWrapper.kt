package org.im.service.client.impl

import org.im.service.log.logDebug
import org.im.service.client.interfaces.Message
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午9:41
 */
class MessageParserFactoryWrapper: Message.ParserFactory {

    fun addDecodeFactory(parserFactory: Message.ParserFactory) {

    }

    override fun parse(jsonObject: JSONObject): Message? {
        logDebug("receive message: $jsonObject")
        return null
    }
}