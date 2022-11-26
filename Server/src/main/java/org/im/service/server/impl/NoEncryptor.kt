package org.im.service.server.impl

import org.im.service.interfaces.IEncryptor


class NoEncryptor: IEncryptor {

    override fun encode(source: String): ByteArray = source.encodeToByteArray()

    override fun decode(byteSource: ByteArray): String = byteSource.decodeToString()
}