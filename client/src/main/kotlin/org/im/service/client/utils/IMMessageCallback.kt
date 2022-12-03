package org.im.service.client.utils

import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.metadata.NotifyWrapper
import org.im.service.client.interfaces.Message
import org.json.JSONObject

fun IMMessageCallback.notifyEvent(code: Int, overriddenBlock: NotifyWrapper.() -> Unit = {}) = kotlin.run {
    val wrapper = NotifyWrapper()
    wrapper.code = code
    with(wrapper, overriddenBlock)
    onNotify(wrapper)
    wrapper
}

fun IMMessageCallback.notifyMessages(code: Int, vararg message: Message) = notifyEvent(code) {
    messages = message.toList()
}

fun IMMessageCallback.notifyJSONObjects(code: Int, vararg jsonObject: JSONObject) = notifyEvent(code) {
    jsonObjects = jsonObject.toList()
}

fun IMMessageCallback.notifySingleType(code: Int) = notifyEvent(code)