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

var customLogger: IMServiceLog?
    set(value) { IMServiceLogger.customLogger = value }
    get() = IMServiceLogger.customLogger

internal object IMServiceLogger: IMServiceLog {
    var customLogger: IMServiceLog? = null

    override fun log(tagName: String, message: String) {
        customLogger?.log(tagName, message)
        if (isDebugLog) {
            println("[$tagName]: $message")
        }
    }
}