package org.im.service.utils

import com.sun.tools.classfile.ConstantPool.CONSTANT_Class_info
import org.im.service.Const
import org.im.service.client.interfaces.Message
import org.im.service.client.metadata.*
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/29 11:27
 */
// {
//     "content": {
//         "sessionType": 1,
//         "toUser": "{\"sid\":\"MTIzNDU1NTIyMw==\"}",
//         "content": "asfasdf",
//         "type": 1,
//         "fromUserId": "123455512",
//         "toUserId": "",
//         "uuid": "9c52dba8-d39c-40ff-a698-9d2dd15665ff",
//         "fromUser": "{\"sid\":\"MTIzNDU1NTEy\"}",
//         "clientExtension": "{}",
//         "remoteExtension": "{}"
//     },
//     "method": "mstx"
// }

/**
 * 获取方法参数，表示当前消息是做什么的
 *
 * Spring可忽略
 */
var JSONObject.method: String
    set(value) { put(Const.Param.PARAM_METHOD, value) }
    get() = optString(Const.Param.PARAM_METHOD)

/**
 * 表示是否是IM登录请求
 *
 * Spring 可忽略
 */
val JSONObject.isAuthorizationRequest: Boolean
    get() = method == Const.Method.USER_AUTHORIZATION

/**
 * JSON消息体里的内容对象，这里会保存具体的消息
 */
val JSONObject.content: JSONObject?
    get() = optJSONObject(Const.Param.PARAM_CONTENT)

/**
 * 从当前JSONObject尝试获取sessionId，没有则返回空字符串
 */
val JSONObject.sessionId: String
    get() = optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

/**
 * 当前消息发送人的uid，可能为空
 */
val JSONObject.fromUserId: String
    get() = content?.optString(Const.Param.PARAM_FROM_USER_ID) ?: ""

/**
 * 当前消息的接收人uid，可能为空
 */
val JSONObject.toUserId: String
    get() = content?.optString(Const.Param.PARAM_TO_USER_ID) ?: ""

val JSONObject.toUser: String
    get() = content?.optString(Const.Param.PARAM_TO_USER) ?: ""

val JSONObject.fromUser: String
    get() = content?.optString(Const.Param.PARAM_FROM_USER) ?: ""

/**
 * 接收者的会话id
 */
val JSONObject.toSessionId: String
    get() = kotlin.runCatching { JSONObject(toUser) }.getOrNull()?.optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

/**
 * 发送者的会话id
 */
val JSONObject.fromSessionId: String
    get() = kotlin.runCatching { JSONObject(fromUser) }.getOrNull()?.optString(Const.Param.PARAM_USER_SESSION_ID) ?: ""

/**
 * 消息的文字内容
 */
val JSONObject.textContent: String
    get() = content?.optString(Const.Param.PARAM_CONTENT) ?: ""

/**
 * 消息的唯一id
 */
val JSONObject.uuid: String
    get() = content?.optString(Const.Param.PARAM_UUID) ?: ""

/**
 * 服务端扩展字段，由服务端设置，为json序列化的字符串
 */
var JSONObject.remoteExtension: String?
    set(value) { content?.put(Const.Param.PARAM_REMOTE_EXTENSION, value) }
    get() = content?.optString(Const.Param.PARAM_REMOTE_EXTENSION)

/**
 * 客户端的扩展字段，由客户端设置，为json序列化的字符串
 */
val JSONObject.clientExtension: String?
    get() = content?.optString(Const.Param.PARAM_CLIENT_EXTENSION)

/**
 * 消息类型，可参考[MsgType]
 */
val JSONObject.type: MsgType
    get() = content?.optInt(Const.Param.PARAM_TYPE, 0)?.let { createMsgTypeByCode(it) } ?: MsgType.Unknown

var JSONObject.msgState: MsgState
    set(value) { content?.put(Const.Param.PARAM_STATE, value.code) }
    get() = content?.optInt(Const.Param.PARAM_STATE, 0)?.let { createMsgStateThroughCode(it) } ?: MsgState.Unknown

/**
 * 会话类型，可参考[SessionType]
 */
val JSONObject.sessionType: SessionType
    get() = content?.optInt(Const.Param.PARAM_SESSION_TYPE, 0)?.let { createSessionTypeByCode(it) } ?: SessionType.Unknown

/**
 * 附件对象，为json序列化的字符串
 */
val JSONObject.attachment: String?
    get() = content?.optString(Const.Param.PARAM_ATTACHMENT)

/**
 * 更新参数
 */
fun JSONObject.updateRemoteExtensionsThroughMap(updateFunc: MutableMap<String, Any?>.() -> Unit) = synchronized(this) {
    val remoteJSONObject = remoteExtension.takeIf { !it.isNullOrEmpty() }
        ?.let { remoteJson -> kotlin.runCatching { JSONObject(remoteJson) }.getOrElse { JSONObject() } }
    val mutableMap = remoteJSONObject?.toMap()
    mutableMap?.updateFunc()
    remoteExtension = mutableMap?.let{ notNullMap -> JSONObject(notNullMap) }?.toString()
}

/**
 * 更新参数
 */
fun JSONObject.updateRemoteExtensionsThroughJSONObject(updateFunc: JSONObject.() -> Unit) = synchronized(this) {
    val remoteJSONObject = remoteExtension.takeIf { !it.isNullOrEmpty() }
        ?.let { remoteJson -> kotlin.runCatching { JSONObject(remoteJson) }.getOrElse { JSONObject() } }
    remoteJSONObject?.updateFunc()
    remoteExtension = remoteJSONObject?.toString()
}

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