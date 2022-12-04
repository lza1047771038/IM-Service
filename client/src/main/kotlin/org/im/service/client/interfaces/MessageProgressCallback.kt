package org.im.service.client.interfaces

interface MessageProgressCallback {
    fun onSuccess(message: Message)
    fun onFailed(message: Message, exception: Exception)
}