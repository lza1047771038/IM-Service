package org.im.service.metadata.client

import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:14
 */
interface Message {
    val uuid: String
    val textContent: String

    val msgType: MsgType

    val fromUser: MsgAccount
    val toUser: MsgAccount

    val remoteExtensions: Map<String, Any?>
    val clientExtensions: Map<String, Any?>

    interface Factory {
        fun decode(jsonObject: JSONObject): Message?
    }
}