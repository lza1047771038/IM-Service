package org.im.service.client.metadata

import org.im.service.metadata.client.Message
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/12/1 17:39
 */
class NotifyWrapper {
    var code: Int = -1
    var messages: List<Message?>? = null
    var jsonObjects: List<JSONObject?>? = null
}