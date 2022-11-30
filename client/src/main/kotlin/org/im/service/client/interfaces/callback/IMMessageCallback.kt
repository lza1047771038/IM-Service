package org.im.service.client.interfaces.callback

import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/30 21:18
 */
interface IMMessageCallback {
    fun onNotify(code: Int, jsonObject: JSONObject?)
}