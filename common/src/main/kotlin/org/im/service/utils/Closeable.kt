package org.im.service.utils

import java.io.Closeable

fun Closeable.closeSilently() {
    kotlin.runCatching {
        close()
    }.onFailure { outerException ->
        outerException.printStackTrace()
        kotlin.runCatching {
            close()
        }.onFailure {
            it.printStackTrace()
        }
    }
}