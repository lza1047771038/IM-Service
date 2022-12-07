package org.im.service.utils

import org.im.service.Const
import org.im.service.client.impl.LoginMessageImpl
import org.im.service.client.interfaces.Message
import org.im.service.client.impl.MessageImpl
import org.im.service.client.metadata.MsgAccount
import org.im.service.client.metadata.MsgType
import org.im.service.client.metadata.SessionType
import org.json.JSONObject
import java.util.UUID

/**
 * @author: liuzhongao
 * @date: 2022/11/29 20:01
 */

fun createEmptyMessage(): Message {
    val uuid = UUID.randomUUID().toString()
    return obtainMessageWithFullParameter(uuid = uuid)
}

fun createEmptyMessage(uuid: String): Message {
    return obtainMessageWithFullParameter(uuid = uuid)
}

fun createLoginMessage(): Message {
    val uuid = UUID.randomUUID().toString()
    return LoginMessageImpl(uuid)
}

fun createTextMessage(textContent: String, type: SessionType = SessionType.P2P): Message {
    val uuid = UUID.randomUUID().toString()
    return obtainMessageWithFullParameter(uuid = uuid, textContent = textContent, sessionType = type, msgType = MsgType.Text)
}

fun createCustomMessage(sessionType: SessionType): Message {
    val uuid = UUID.randomUUID().toString()
    return obtainMessageWithFullParameter(uuid = uuid, sessionType = sessionType, msgType = MsgType.Custom)
}

fun obtainTextMessageWithFullParameter(
    uuid: String = UUID.randomUUID().toString(),
    textContent: String = "",
    fromSessionId: String = "",
    toSessionId: String = "",
    fromUserId: String = "",
    toUserId: String = "",
    remoteExtension: MutableMap<String, Any?>? = null,
    clientExtension: MutableMap<String, Any?>? = null,
    sessionType: SessionType,
): Message {
    return obtainMessageWithFullParameter(
        uuid = uuid,
        textContent = textContent,
        fromSessionId = fromSessionId,
        toSessionId = toSessionId,
        fromUserId = fromUserId,
        toUserId = toUserId,
        remoteExtension = remoteExtension,
        clientExtension = clientExtension,
        sessionType = sessionType,
        msgType = MsgType.Text
    )
}

fun obtainMessageWithFullParameter(
    uuid: String,
    textContent: String = "",
    fromSessionId: String = "",
    toSessionId: String = "",
    fromUserId: String = "",
    toUserId: String = "",
    remoteExtension: MutableMap<String, Any?>? = null,
    clientExtension: MutableMap<String, Any?>? = null,
    sessionType: SessionType = SessionType.Unknown,
    msgType: MsgType = MsgType.Unknown
): Message {
    val message = MessageImpl(uuid)

    message.textContent = textContent
    message.fromUser = MsgAccount(fromSessionId)
    message.toUser = MsgAccount(toSessionId)
    message.fromUserId = fromUserId
    message.toUserId = toUserId
    message.remoteExtensions = remoteExtension
    message.clientExtensions = clientExtension
    message.sessionType = sessionType
    message.msgType = msgType

    return message
}

fun Message.toJSONObj(method: String): JSONObject {
    return JSONObject().parseIMMessage(method, this)
}

fun Message.toJSONObj(): JSONObject {
    // todo: 此处逻辑待优化
    return when (this) {
        is LoginMessageImpl -> Const.Method.USER_AUTHORIZATION
        else -> Const.Method.MESSAGE_TEXT
    }.let { method -> JSONObject().parseIMMessage(method, this) }
}
