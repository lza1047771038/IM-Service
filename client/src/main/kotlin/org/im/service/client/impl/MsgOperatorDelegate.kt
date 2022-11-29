package org.im.service.client.impl

import org.im.service.client.interfaces.SessionOperator
import org.im.service.client.utils.IMUserInfo
import org.im.service.metadata.TransportObj
import org.im.service.metadata.fromUser
import org.im.service.metadata.fromUserId

/**
 * @author: liuzhongao
 * @date: 2022/11/29 15:06
 */
abstract class MsgSessionDelegate: SessionOperator {

    // append self user info
    override fun sendMessage(transportObj: TransportObj) {
        transportObj.fromUserId = IMUserInfo.selfUserId
        transportObj.fromUser = IMUserInfo.selfAccount
        realSendMessage(transportObj)
    }

    abstract fun realSendMessage(transportObj: TransportObj)
}