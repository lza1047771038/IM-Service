package org.im.service.log

import org.im.service.interfaces.Logger

/**
 * @author: liuzhongao
 * @date: 2022/11/28 13:21
 */
var isDebugLog: Boolean = false

val logger: Logger
    get() = IMServiceLogger

fun Any.logDebug(message: String) {
    logger.log(this.javaClass.simpleName, message)
}

fun Any.logError(message: String) {
    logger.log(this.javaClass.simpleName, message, Logger.Type.Error)
}

fun Logger.logDebug(tagName: String, message: String) {
    log(tagName, message, Logger.Type.Normal)
}

fun Logger.logError(tagName: String, message: String) {
    log(tagName, message, Logger.Type.Error)
}

var customLogger: Logger?
    set(value) { IMServiceLogger.customLogger = value }
    get() = IMServiceLogger.customLogger

internal object IMServiceLogger: Logger {
    var customLogger: Logger? = null

    override fun log(tagName: String, message: String) {
        this.log(tagName, message, Logger.Type.Normal)
    }

    override fun log(tagName: String, message: String, type: Logger.Type) {
        customLogger?.log(tagName, message)
        if (isDebugLog) {
            when (type) {
                Logger.Type.Normal -> System.out
                Logger.Type.Error -> System.err
                else -> System.out
            }.println("[$tagName]: $message")
        }
    }
}