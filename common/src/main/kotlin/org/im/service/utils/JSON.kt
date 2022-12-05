package org.im.service.utils

import org.im.service.Const
import org.im.service.client.interfaces.Message
import org.im.service.client.metadata.MsgType
import org.im.service.client.metadata.SessionType
import org.im.service.client.metadata.createMsgTypeByCode
import org.im.service.client.metadata.createSessionTypeByCode
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/29 11:27
 */

val JSONObject.method: String
    get() = optString(Const.Param.PARAM_METHOD)

val JSONObject.isAuthorizationRequest: Boolean
    get() = method == Const.Method.USER_AUTHORIZATION

val JSONObject.content: JSONObject?
    get() = optJSONObject(Const.Param.PARAM_CONTENT)

val JSONObject.sessionId: String
    get() = optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

val JSONObject.fromUserId: String
    get() = content?.optString(Const.Param.PARAM_FROM_USER_ID) ?: ""

val JSONObject.toUserId: String
    get() = content?.optString(Const.Param.PARAM_TO_USER_ID) ?: ""

val JSONObject.toUser: String
    get() = content?.optString(Const.Param.PARAM_TO_USER) ?: ""

val JSONObject.fromUser: String
    get() = content?.optString(Const.Param.PARAM_FROM_USER) ?: ""

val JSONObject.toSessionId: String
    get() = kotlin.runCatching { JSONObject(toUser) }.getOrNull()?.optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

val JSONObject.fromSessionId: String
    get() = kotlin.runCatching { JSONObject(fromUser) }.getOrNull()?.optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

val JSONObject.textContent: String
    get() = content?.optString(Const.Param.PARAM_CONTENT) ?: ""

val JSONObject.uuid: String
    get() = content?.optString(Const.Param.PARAM_UUID) ?: ""

val JSONObject.remoteExtension: String?
    get() = content?.optString(Const.Param.PARAM_REMOTE_EXTENSION)

val JSONObject.clientExtension: String?
    get() = content?.optString(Const.Param.PARAM_CLIENT_EXTENSION)

val JSONObject.type: MsgType
    get() = content?.optInt(Const.Param.PARAM_TYPE, 0)?.let { createMsgTypeByCode(it) } ?: MsgType.Unknown

val JSONObject.sessionType: SessionType
    get() = content?.optInt(Const.Param.PARAM_SESSION_TYPE, 0)?.let { createSessionTypeByCode(it) } ?: SessionType.Unknown

val JSONObject.attachment: String?
    get() = content?.optString(Const.Param.PARAM_ATTACHMENT)

internal fun JSONObject.parseIMMessage(method: String, message: Message) = apply {
    val content = JSONObject()
    put(Const.Param.PARAM_METHOD, method)
    put(Const.Param.PARAM_CONTENT, content)

    content.put(Const.Param.PARAM_UUID, message.uuid)
    content.put(Const.Param.PARAM_CONTENT, message.textContent)

    content.put(Const.Param.PARAM_FROM_USER_ID, message.fromUserId)
    content.put(Const.Param.PARAM_TO_USER_ID, message.toUserId)

    content.put(Const.Param.PARAM_SESSION_TYPE, message.sessionType.code)
    content.put(Const.Param.PARAM_TYPE, message.msgType.code)

    // put client & remote extensions
    val clientExtString = message.clientExtensions.takeIf { it != null && it.isNotEmpty() }?.let { JSONObject(it) }?.toString()
    val remoteExtString = message.remoteExtensions.takeIf { it != null && it.isNotEmpty() }?.let { JSONObject(it) }?.toString()
    content.put(Const.Param.PARAM_CLIENT_EXTENSION, clientExtString)
    content.put(Const.Param.PARAM_REMOTE_EXTENSION, remoteExtString)

    // put msgAccount
    content.put(Const.Param.PARAM_FROM_USER, message.fromUser?.toJson())
    content.put(Const.Param.PARAM_TO_USER, message.toUser?.toJson())

    // put attachment string
    content.put(Const.Param.PARAM_ATTACHMENT, message.attachment?.toJson())
}