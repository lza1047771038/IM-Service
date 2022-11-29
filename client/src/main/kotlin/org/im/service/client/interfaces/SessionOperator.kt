package org.im.service.client.interfaces

import org.im.service.Const
import org.im.service.metadata.TransportObj
import org.im.service.metadata.textContent

/**
 * @author: liuzhongao
 * @date: 2022/11/29 13:55
 */
interface SessionOperator {
    fun sendMessage(transportObj: TransportObj)
}

fun SessionOperator.sendTextMessage(content: String) {
    val transportObj = TransportObj(method = Const.Method.MESSAGE_TEXT)
    transportObj.textContent = content
    sendMessage(transportObj)
}