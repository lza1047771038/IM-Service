package org.im.service.interfaces

import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/28 下午9:30
 */
interface ResponseHandler {
    fun handle(method: String, jsonObject: JSONObject)
}