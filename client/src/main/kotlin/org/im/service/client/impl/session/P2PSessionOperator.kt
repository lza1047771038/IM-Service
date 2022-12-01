package org.im.service.client.impl.session

import org.im.service.client.interfaces.SessionOperator
import org.im.service.metadata.client.Message
import org.im.service.metadata.client.MsgAccount

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:56
 */
class P2PSessionOperator(
    private val targetUserSessionId: String,
    private val rawSessionOperator: SessionOperator
): SessionOperator {

    private val toUserAccount by lazy { MsgAccount(targetUserSessionId) }

    override fun sendMessage(message: Message) {
        message.toUser = toUserAccount
        rawSessionOperator.sendMessage(message)
    }
}