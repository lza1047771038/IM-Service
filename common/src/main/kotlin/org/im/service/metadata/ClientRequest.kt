package org.im.service.metadata

import org.im.service.Const
import org.im.service.moshi.moshi

data class ClientRequest(
    val method: String,
    val params: MutableMap<String, Any?> = mutableMapOf()
)

fun ClientRequest.toJson(): String {
    return "${moshi.adapter(ClientRequest::class.java).toJson(this)}\n"
}

val ClientRequest.isAuthorizationRequest: Boolean
    get() = method == Const.RequestMethod.USER_AUTHORIZATION

var ClientRequest.content: String
    set(value) { params[Const.Param.PARAM_CONTENT] = value }
    get() = params[Const.Param.PARAM_CONTENT] as? String ?: ""

var ClientRequest.clientSessionId: String
    set(value) { params[Const.Param.PARAM_USER_SESSION_ID] = value }
    get() = params[Const.Param.PARAM_USER_SESSION_ID] as? String ?: ""

var ClientRequest.clientUserId: String
    set(value) { params[Const.Param.PARAM_USER_ID] = value }
    get() = params[Const.Param.PARAM_USER_ID] as? String ?: ""

var ClientRequest.toUserSessionId: String
    set(value) { params[Const.Param.PARAM_USER_ID] = value }
    get() = params[Const.Param.PARAM_USER_ID] as? String ?: ""
