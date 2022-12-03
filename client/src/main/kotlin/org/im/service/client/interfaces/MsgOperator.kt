package org.im.service.client.interfaces

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:57
 */
interface MsgOperator {
    fun openSession(sessionId: String): SessionOperator
}