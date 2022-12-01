package org.im.service.metadata.client

import org.im.service.metadata.SessionType
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:14
 */
interface Message {
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

    interface DecodeFactory {
        fun decode(jsonObject: JSONObject): Message?
    }
}