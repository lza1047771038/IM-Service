package org.im.service.client.interfaces

import org.im.service.metadata.SessionType
import org.im.service.metadata.client.Message
import org.im.service.utils.createEmptyMessage
import org.im.service.utils.createTextMessage

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:55
 */
interface SessionOperator {
    fun sendMessage(message: Message)
}

fun SessionOperator.sendTextMessage(content: String, sessionType: SessionType) {
    val message = createTextMessage(content, sessionType)
    message.textContent = content
    sendMessage(message)
}