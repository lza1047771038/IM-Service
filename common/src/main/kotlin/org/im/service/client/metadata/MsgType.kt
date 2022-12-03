package org.im.service.client.metadata

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:40
 */
enum class MsgType(val code: Int) {
    Unknown(0),
    Text(1),
    Audio(2),
    Image(3),
    Custom(4)
}

fun createMsgTypeByCode(code: Int): MsgType {
    return when (code) {
        MsgType.Text.code -> MsgType.Text
        MsgType.Audio.code -> MsgType.Audio
        MsgType.Image.code -> MsgType.Image
        MsgType.Custom.code -> MsgType.Custom
        else -> MsgType.Unknown
    }
}