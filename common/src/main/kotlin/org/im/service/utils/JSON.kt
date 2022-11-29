package org.im.service.utils

import org.im.service.Const
import org.im.service.metadata.client.MsgType
import org.im.service.metadata.client.createMsgTypeByCode
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

val JSONObject.fromUserId: String
    get() = content?.optString(Const.Param.PARAM_FROM_USER_ID) ?: ""

val JSONObject.toUserId: String
    get() = content?.optString(Const.Param.PARAM_TO_USER_ID) ?: ""

val JSONObject.toUser: JSONObject?
    get() = content?.optJSONObject(Const.Param.PARAM_TO_USER)

val JSONObject.fromUser: JSONObject?
    get() = content?.optJSONObject(Const.Param.PARAM_FROM_USER)

val JSONObject.toUserSessionId: String
    get() = toUser?.optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

val JSONObject.fromUserSessionId: String
    get() = fromUser?.optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

val JSONObject.textContent: String
    get() = content?.optString(Const.Param.PARAM_CONTENT) ?: ""

val JSONObject.uuid: String
    get() = content?.optString(Const.Param.PARAM_UUID) ?: ""

val JSONObject.removeExtension: JSONObject?
    get() = content?.optJSONObject(Const.Param.PARAM_REMOTE_EXTENSION)

val JSONObject.clientExtension: JSONObject?
    get() = content?.optJSONObject(Const.Param.PARAM_CLIENT_EXTENSION)

val JSONObject.type: MsgType
    get() = content?.optInt(Const.Param.PARAM_TYPE, 0)?.let { createMsgTypeByCode(it) } ?: MsgType.Unknown

val JSONObject.attachment: JSONObject?
    get() = content?.optJSONObject(Const.Param.PARAM_ATTACHMENT)
