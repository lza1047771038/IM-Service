package org.im.service.client.impl.session

import org.im.service.client.interfaces.SessionOperator
import org.im.service.client.interfaces.Message
import org.im.service.client.interfaces.MessageProgressCallback
import org.im.service.client.metadata.MsgAccount

/**
 * @author: liuzhongao
 * @date: 2022/12/2 09:54
 */
internal class GroupSessionOperator(
    private val targetUserSessionId: String,
    private val rawSessionOperator: SessionOperator
): SessionOperator {

    private val toGroupAccount by lazy { MsgAccount(targetUserSessionId) }

    override fun sendMessage(message: Message) = sendMessage(message, null)

    override fun sendMessage(message: Message, messageProgressCallback: MessageProgressCallback?) {
        message.toUser = toGroupAccount
        rawSessionOperator.sendMessage(message)
    }

    override fun deleteMessage(message: Message) = rawSessionOperator.deleteMessage(message)
}