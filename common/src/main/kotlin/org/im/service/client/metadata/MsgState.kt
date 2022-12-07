package org.im.service.client.metadata

/**
 * @author: liuzhongao
 * @date: 2022/12/7 14:06
 */
enum class MsgState(val code: Int) {
    Unknown(-1),
    Sending(1),
    Success(2),
    Failed(3)
}

fun createMsgStateThroughCode(code: Int): MsgState {
    return when (code) {
        MsgState.Unknown.code -> MsgState.Unknown
        MsgState.Sending.code -> MsgState.Sending
        MsgState.Success.code -> MsgState.Success
        MsgState.Failed.code -> MsgState.Failed
        else -> MsgState.Unknown
    }
}