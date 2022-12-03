package org.im.service.client.interfaces

import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/29 20:26
 */
interface Attachment {
    fun toJSONObject(): JSONObject
}
