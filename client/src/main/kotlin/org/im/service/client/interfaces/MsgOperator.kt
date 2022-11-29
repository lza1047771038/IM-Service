package org.im.service.client.interfaces

import org.im.service.metadata.SessionType

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:57
 */
interface MsgOperator {
    fun openSession(sessionId: String, type: SessionType): SessionOperator
}