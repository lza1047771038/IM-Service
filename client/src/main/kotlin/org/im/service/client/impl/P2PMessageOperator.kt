package org.im.service.client.impl

import org.im.service.client.impl.session.GroupSessionOperator
import org.im.service.client.impl.session.P2PSessionOperator
import org.im.service.client.interfaces.Message
import org.im.service.client.interfaces.MessageProgressCallback
import org.im.service.client.interfaces.MsgOperator
import org.im.service.client.interfaces.SessionOperator

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:59
 */
class P2PMessageOperator(
    private val rawSessionOperator: SessionOperator
): MsgOperator {
    override fun openSession(sessionId: String): SessionOperator {
        return P2PSessionOperator(sessionId, rawSessionOperator)
    }

    override fun sendMessage(message: Message) = rawSessionOperator.sendMessage(message)

    override fun sendMessage(message: Message, messageProgressCallback: MessageProgressCallback?) = rawSessionOperator.sendMessage(message, messageProgressCallback)

    override fun deleteMessage(message: Message) = rawSessionOperator.deleteMessage(message)
}

class GroupMessageOperator(
    private val rawSessionOperator: SessionOperator
): MsgOperator {
    override fun openSession(sessionId: String): SessionOperator {
        return GroupSessionOperator(sessionId, rawSessionOperator)
    }

    override fun sendMessage(message: Message) = rawSessionOperator.sendMessage(message)

    override fun sendMessage(message: Message, messageProgressCallback: MessageProgressCallback?) = rawSessionOperator.sendMessage(message, messageProgressCallback)

    override fun deleteMessage(message: Message) = rawSessionOperator.deleteMessage(message)
}