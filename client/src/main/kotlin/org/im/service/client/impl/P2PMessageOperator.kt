package org.im.service.client.impl

import org.im.service.client.impl.session.GroupSessionOperator
import org.im.service.client.impl.session.P2PSessionOperator
import org.im.service.client.interfaces.MsgOperator
import org.im.service.client.interfaces.SessionOperator
import org.im.service.metadata.SessionType

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

    override fun closeSession(sessionId: String) {

    }
}

class GroupMessageOperator(
    private val rawSessionOperator: SessionOperator
): MsgOperator {
    override fun openSession(sessionId: String): SessionOperator {
        return GroupSessionOperator(sessionId, rawSessionOperator)
    }

    override fun closeSession(sessionId: String) {

    }
}