package org.im.service.interfaces

import org.im.service.metadata.ClientRequest
import java.nio.channels.SocketChannel

interface RequestHandler {
    fun handle(socketChannel: SocketChannel, request: ClientRequest)
}
