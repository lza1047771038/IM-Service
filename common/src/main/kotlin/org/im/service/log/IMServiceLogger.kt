package org.im.service.log

import org.im.service.interfaces.IMServiceLog

/**
 * @author: liuzhongao
 * @date: 2022/11/28 13:21
 */
var isDebugLog: Boolean = false

val logger: IMServiceLog
    get() = IMServiceLogger

fun Any.logDebug(message: String) {
    logger.log(this.javaClass.simpleName, message)
}

internal object IMServiceLogger: IMServiceLog {
    override fun log(tagName: String, message: String) {
        if (isDebugLog) {
            println("[$tagName]: $message")
        }
    }
}