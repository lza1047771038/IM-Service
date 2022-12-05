package org.im.service.client.interfaces

import org.im.service.client.metadata.MsgAccount
import org.im.service.client.metadata.MsgType
import org.im.service.client.metadata.SessionType
import org.json.JSONObject
import java.io.Serializable

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:14
 */
interface Message: Serializable {
    val uuid: String
    var textContent: String

    var fromUserId: String
    var toUserId: String

    var msgType: MsgType
    var sessionType: SessionType

    var fromUser: MsgAccount?
    var toUser: MsgAccount?

    var remoteExtensions: MutableMap<String, Any?>?
    var clientExtensions: MutableMap<String, Any?>?

    var attachment: Attachment?

    interface AttachmentParserFactory {
        fun parse(jsonObject: JSONObject?): Attachment?
    }
}