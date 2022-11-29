package org.im.service.utils

import org.im.service.Const
import org.im.service.metadata.*
import org.im.service.metadata.client.LoginMessageImpl
import org.im.service.metadata.client.Message
import org.im.service.metadata.client.MessageImpl
import org.im.service.metadata.client.MsgType

/**
 * @author: liuzhongao
 * @date: 2022/11/29 20:01
 */

fun createEmptyMessage(): Message {
    return MessageImpl()
}

fun createLoginMessage(): Message {
    return LoginMessageImpl()
}

fun createTextMessage(textContent: String, type: SessionType = SessionType.P2P): Message {
    val message = MessageImpl()
    message.textContent = textContent
    message.msgType = MsgType.Text
    message.sessionType = type
    return message
}

fun createCustomMessage(sessionType: SessionType): Message {
    val message = MessageImpl()
    message.msgType = MsgType.Custom
    message.sessionType = sessionType
    return message
}

fun Message.toTransportObj(): TransportObj {
    return when (this) {
        is LoginMessageImpl -> Const.Method.USER_AUTHORIZATION
        else -> Const.Method.MESSAGE_TEXT
    }.let { method -> TransportObj(method = method) }.also { obj ->
        obj.uuid = uuid
        obj.textContent = textContent
        obj.fromUserId = fromUserId
        obj.toUserId = toUserId
        obj.fromUser = fromUser
        obj.toUser = toUser
        obj.remoteExtension = remoteExtensions
        obj.clientExtension = clientExtensions
        obj.type = msgType
        obj.sessionType = sessionType
    }
}