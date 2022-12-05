package org.im.service.client.interfaces

import org.jetbrains.annotations.TestOnly

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:57
 */
interface MsgOperator {
    @TestOnly
    fun openSession(sessionId: String): SessionOperator

    fun sendMessage(message: Message)
    fun sendMessage(message: Message, messageProgressCallback: MessageProgressCallback?)
    fun deleteMessage(message: Message)
}