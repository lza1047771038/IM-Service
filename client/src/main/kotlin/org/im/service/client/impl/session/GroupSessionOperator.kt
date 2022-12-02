package org.im.service.client.impl.session

import org.im.service.client.interfaces.SessionOperator
import org.im.service.metadata.client.Message
import org.im.service.metadata.client.MsgAccount

/**
 * @author: liuzhongao
 * @date: 2022/12/2 09:54
 */
class GroupSessionOperator(
    private val targetUserSessionId: String,
    private val rawSessionOperator: SessionOperator
): SessionOperator {

    private val toGroupAccount by lazy { MsgAccount(targetUserSessionId) }

    override fun sendMessage(message: Message) {
        message.toUser = toGroupAccount
        rawSessionOperator.sendMessage(message)
    }

    override fun deleteMessage(message: Message) {

    }
}