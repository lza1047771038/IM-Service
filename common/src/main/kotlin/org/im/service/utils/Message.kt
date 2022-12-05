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
    return createEmptyMessage(uuid)
}

fun createEmptyMessage(uuid: String): Message {
    return MessageImpl(uuid)
}

fun createLoginMessage(): Message {
    val uuid = UUID.randomUUID().toString()
    return LoginMessageImpl(uuid)
}

fun createTextMessage(textContent: String, type: SessionType = SessionType.P2P): Message {
    val message = createEmptyMessage()
    message.textContent = textContent
    message.msgType = MsgType.Text
    message.sessionType = type
    return message
}

fun createCustomMessage(sessionType: SessionType): Message {
    val message = createEmptyMessage()
    message.msgType = MsgType.Custom
    message.sessionType = sessionType
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
