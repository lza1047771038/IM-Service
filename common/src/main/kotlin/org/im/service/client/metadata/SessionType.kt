package org.im.service.client.metadata

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:57
 */
enum class SessionType(val code: Int) {
    Unknown(0),
    P2P(1),
    Group(2)
}

fun createSessionTypeByCode(code: Int): SessionType {
    return when (code) {
        SessionType.P2P.code -> SessionType.P2P
        SessionType.Group.code -> SessionType.Group
        else -> SessionType.Unknown
    }
}