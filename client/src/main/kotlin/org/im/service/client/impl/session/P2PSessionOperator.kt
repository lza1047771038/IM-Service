package org.im.service.client.impl.session

import org.im.service.client.interfaces.SessionOperator
import org.im.service.client.utils.IMUserInfo
import org.im.service.metadata.TransportObj
import org.im.service.metadata.client.MsgAccount
import org.im.service.metadata.fromUser
import org.im.service.metadata.fromUserId
import org.im.service.metadata.toUser

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:56
 */
class P2PSessionOperator(
    private val targetUserSessionId: String,
    private val rawSessionOperator: SessionOperator
): SessionOperator {

    private val toUserAccount by lazy { MsgAccount(targetUserSessionId) }

    override fun sendMessage(transportObj: TransportObj) {
        transportObj.toUser = toUserAccount
        rawSessionOperator.sendMessage(transportObj)
    }
}