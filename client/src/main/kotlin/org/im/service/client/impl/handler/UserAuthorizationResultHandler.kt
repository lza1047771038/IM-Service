package org.im.service.client.impl.handler

import org.im.service.Const
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.utils.IMUserInfo
import org.im.service.client.utils.notifyJSONObjects
import org.im.service.interfaces.ResponseHandler
import org.im.service.utils.remoteExtension
import org.im.service.utils.sessionId
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:45
 */
class UserAuthorizationResultHandler(
    private val callback: IMMessageCallback
): ResponseHandler {
    override fun handle(method: String, jsonObject: JSONObject) {
        val remoteExtString = jsonObject.remoteExtension
        val sessionId = remoteExtString?.let { kotlin.runCatching { JSONObject(it) }.getOrNull() }?.sessionId
        if (sessionId.isNullOrEmpty()) {
            callback.notifyJSONObjects(Const.Code.SESSION_AUTHORIZATION_FAILED, jsonObject)
        } else {
            IMUserInfo.selfSessionId = sessionId
            callback.notifyJSONObjects(Const.Code.SESSION_AUTHORIZATION_SUCCESS, jsonObject)
        }
    }
}