package org.im.service.metadata

import org.im.service.Const
import org.im.service.moshi.moshi
import java.net.HttpURLConnection


data class ServerResponse(
    val code: Int = HttpURLConnection.HTTP_OK,
    val method: String,
    val content: MutableMap<String, Any?> = mutableMapOf(),
    val message: String = ""
) {
    companion object {
        @JvmStatic
        fun success(method: String, data: MutableMap<String, Any?> = mutableMapOf()): ServerResponse {
            return ServerResponse(method = method, content = data)
        }

        @JvmStatic
        fun success(method: String, dataBlock: MutableMap<String, Any?>.() -> Unit): ServerResponse {
            val dataMap = HashMap<String, Any?>()
            dataMap.dataBlock()
            return success(method, dataMap)
        }

        @JvmStatic
        fun error(method: String, message: String, code: Int, data: MutableMap<String, Any?> = mutableMapOf()): ServerResponse {
            return ServerResponse(code = code, method = method, content = data, message = message)
        }
    }
}

fun ServerResponse.toJson(): String {
    return moshi.adapter(ServerResponse::class.java).toJson(this)
}

var ServerResponse.sessionId: String
    set(value) {
        content[Const.Param.PARAM_USER_SESSION_ID] = value}
    get() = content[Const.Param.PARAM_USER_SESSION_ID] as? String ?: ""