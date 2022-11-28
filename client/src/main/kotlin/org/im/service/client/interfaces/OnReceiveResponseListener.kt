package org.im.service.client.interfaces

import org.im.service.metadata.ServerResponse

interface OnReceiveResponseListener {
    fun onReceive(response: ServerResponse)
}