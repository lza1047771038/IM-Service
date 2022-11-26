package org.im.service.interfaces

interface IEncryptor {
    fun encode(source: String): ByteArray
    fun decode(byteSource: ByteArray): String
}