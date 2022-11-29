package org.im.service.metadata

import org.im.service.Const
import org.im.service.metadata.client.MsgAccount
import org.im.service.metadata.client.MsgType
import org.im.service.metadata.client.createMsgTypeByCode
import org.im.service.moshi.moshi

/**
 * @author: liuzhongao
 * @date: 2022/11/29 11:19
 */

// {
//     "method": "",
//     "conent": {
//         "fromUserId": "",
//         "toUserId": "",
//         "toUSer": {
//             "sessionId": ""
//         },
//         "fromUser": {
//             "sessionId": ""
//         },
//         "content": "",
//         "uuid": "",
//         "remoteExt": {},
//         "clientExt": {},
//         "type": 1,
//         "attachment": {}
//     }
// }

data class TransportObj(
    val method: String,
    val content: MutableMap<String, Any?> = mutableMapOf()
)

fun TransportObj.toJson(): String {
    return moshi.adapter(TransportObj::class.java).toJson(this)
}

var TransportObj.fromUserId: String
    set(value) { content[Const.Param.PARAM_FROM_USER_ID] = value }
    get() = content[Const.Param.PARAM_FROM_USER_ID] as? String ?: ""

var TransportObj.toUserId: String
    set(value) { content[Const.Param.PARAM_TO_USER_ID] = value }
    get() = content[Const.Param.PARAM_TO_USER_ID] as? String ?: ""

var TransportObj.fromUser: MsgAccount?
    set(value) { content[Const.Param.PARAM_FROM_USER] = value }
    get() = content[Const.Param.PARAM_FROM_USER] as? MsgAccount

var TransportObj.toUser: MsgAccount?
    set(value) { content[Const.Param.PARAM_TO_USER] = value }
    get() = content[Const.Param.PARAM_TO_USER] as? MsgAccount

var TransportObj.textContent: String
    set(value) { content[Const.Param.PARAM_CONTENT] = value }
    get() = content[Const.Param.PARAM_CONTENT] as? String ?: ""

var TransportObj.uuid: String
    set(value) { content[Const.Param.PARAM_UUID] = value }
    get() = content[Const.Param.PARAM_UUID] as? String ?: ""

var TransportObj.remoteExtension: MutableMap<String, Any?>?
    set(value) { content[Const.Param.PARAM_REMOTE_EXTENSION] = value }
    get() = content[Const.Param.PARAM_REMOTE_EXTENSION] as? MutableMap<String, Any?>

var TransportObj.clientExtension: MutableMap<String, Any?>?
    set(value) { content[Const.Param.PARAM_CLIENT_EXTENSION] = value }
    get() = content[Const.Param.PARAM_CLIENT_EXTENSION] as? MutableMap<String, Any?>

var TransportObj.type: MsgType
    set(value) { content[Const.Param.PARAM_TYPE] = value.code }
    get() = (content[Const.Param.PARAM_TYPE] as? Int)?.let { createMsgTypeByCode(it) } ?: MsgType.Unknown

var TransportObj.sessionType: SessionType
    set(value) { content[Const.Param.PARAM_SESSION_TYPE] = value.code }
    get() = (content[Const.Param.PARAM_SESSION_TYPE] as? Int)?.let { createSessionTypeByCode(it) } ?: SessionType.Unknown

var TransportObj.attachment: MutableMap<String, Any?>?
    set(value) { content[Const.Param.PARAM_ATTACHMENT] = value }
    get() = content[Const.Param.PARAM_ATTACHMENT] as? MutableMap<String, Any?>
