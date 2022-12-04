package org.im.service.client.interfaces

import org.im.service.client.metadata.SessionType
import org.im.service.utils.createTextMessage

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:55
 */
interface SessionOperator {
    fun sendMessage(message: Message)
    fun sendMessage(message: Message, messageProgressCallback: MessageProgressCallback?)
    fun deleteMessage(message: Message)
}

fun SessionOperator.sendTextMessage(content: String, sessionType: SessionType) {
    val message = createTextMessage(content, sessionType)
    message.textContent = content
    sendMessage(message)
}