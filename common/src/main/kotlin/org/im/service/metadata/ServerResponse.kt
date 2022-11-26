package org.im.service.metadata

import org.im.service.Const
import org.im.service.moshi.moshi
import java.net.HttpURLConnection


data class ServerResponse(
    val code: Int = HttpURLConnection.HTTP_OK,
    val method: String,
    val data: MutableMap<String, Any?> = mutableMapOf(),
    val message: String = ""
)

fun ServerResponse.toJson(): String {
    return moshi.adapter(ServerResponse::class.java).toJson(this)
}

var ServerResponse.clientToken: String
    set(value) {data[Const.Param.PARAM_USER_TOKEN] = value}
    get() = data[Const.Param.PARAM_USER_TOKEN] as? String ?: ""