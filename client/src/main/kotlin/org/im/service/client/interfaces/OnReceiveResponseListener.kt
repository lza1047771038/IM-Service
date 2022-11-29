package org.im.service.client.interfaces

import org.json.JSONObject

interface OnReceiveResponseListener {
    fun onReceive(message: JSONObject)
}