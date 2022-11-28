package org.im.service.server.controller.config

import org.im.service.interfaces.IEncryptor
import org.im.service.impl.NoEncryptor

class SocketConfig {
    var address: String = ""
    var ports: IntArray = intArrayOf(8080)
    var encryptor: IEncryptor = NoEncryptor()
}