package org.im.service.interfaces

import org.json.JSONObject
import java.nio.channels.SocketChannel

interface RequestHandler {
    fun handle(method: String, jsonObject: JSONObject, socketChannel: SocketChannel)
}
