package org.im.service.interfaces

interface SocketServerService {
    val port: Int

    fun startListeningToTargetPort()

    fun stopListeningToTargetPort()

}