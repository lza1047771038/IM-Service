package org.im.service.client.impl.handler

import org.im.service.Const
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.metadata.NotifyWrapper
import org.im.service.client.utils.IMUserInfo
import org.im.service.interfaces.ResponseHandler
import org.im.service.utils.fromUserSessionId
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午10:45
 */
class UserAuthorizationResultHandler(
    private val callback: IMMessageCallback
): ResponseHandler {
    override fun handle(method: String, jsonObject: JSONObject) {
        val sessionId = jsonObject.fromUserSessionId
        IMUserInfo.selfSessionId = sessionId

        val wrapper = NotifyWrapper()
        wrapper.code = Const.Code.SESSION_AUTHORIZATION_SUCCESS
        wrapper.jsonObjects = listOf(jsonObject)
        callback.onNotify(wrapper)
    }
}