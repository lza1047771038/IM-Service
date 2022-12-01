package org.im.service.metadata.client

import org.im.service.Const
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:15
 */
data class MsgAccount(
    val sessionId: String = ""
) {
    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(Const.Param.PARAM_USER_SESSION_ID, sessionId)
        return jsonObject
    }
}