package org.im.service.client.impl.handler

import org.im.service.client.interfaces.GlobalCallback
import org.im.service.interfaces.ResponseHandler
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:45
 */
class UserAuthorizationResultHandler(
    private val callback: GlobalCallback
): ResponseHandler {
    override fun handle(method: String, jsonObject: JSONObject) {
        val sessionId = jsonObject.optJSONObject("content")?.optString("sessionId") ?: ""
        callback.onServiceLoginSuccess(sessionId)
    }
}