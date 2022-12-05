package org.im.service.client.metadata

import org.im.service.Const
import org.im.service.utils.sessionId
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:15
 */
data class MsgAccount(
    val sessionId: String = ""
) {
    companion object {
        @JvmStatic
        fun parseAccount(obj: JSONObject?): MsgAccount? {
            obj ?: return null
            val sessionId = obj.sessionId
            return MsgAccount(sessionId)
        }
    }

    fun toJson(): String {
        val jsonObject = JSONObject()
        jsonObject.put(Const.Param.PARAM_USER_SESSION_ID, sessionId)
        return jsonObject.toString()
    }
}