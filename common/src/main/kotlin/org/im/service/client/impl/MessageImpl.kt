package org.im.service.client.impl

import org.im.service.client.interfaces.Attachment
import org.im.service.client.interfaces.Message
import org.im.service.client.metadata.MsgAccount
import org.im.service.client.metadata.MsgType
import org.im.service.client.metadata.SessionType

/**
 * @author: liuzhongao
 * @date: 2022/11/29 19:58
 */
internal open class MessageImpl(override val uuid: String): Message {
    override var textContent: String = ""
    override var fromUserId: String = ""
    override var toUserId: String = ""
    override var msgType: MsgType = MsgType.Unknown
    override var sessionType: SessionType = SessionType.P2P
    override var fromUser: MsgAccount? = null
    override var toUser: MsgAccount? = null
    override var remoteExtensions: MutableMap<String, Any?>? = null
    override var clientExtensions: MutableMap<String, Any?>? = null
    override var attachment: Attachment? = null
}