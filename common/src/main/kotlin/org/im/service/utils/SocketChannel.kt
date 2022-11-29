package org.im.service.utils

import org.im.service.interfaces.IEncryptor
import org.im.service.log.logDebug
import org.im.service.metadata.TransportObj
import org.im.service.metadata.toJson
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

private val decoder = Charsets.UTF_8

typealias DisconnectedCallback = SocketChannel.() -> Unit

fun SocketChannel.decodeByteArray(byteBuffer: ByteBuffer?): ByteArray? {
    if (byteBuffer == null || !isConnected || !isOpen) {
        return null
    }
    return synchronized(byteBuffer) {
        byteBuffer.clear()

        kotlin.runCatching {
            var flag = 0
            while (true) {
                flag = read(byteBuffer)
                if (flag == 0 || flag == -1) {
                    break
                }
            }
            if (flag == -1) {
                close()
            }
        }.onFailure { outerException ->
            kotlin.runCatching {
                close()
            }.onFailure { innerException ->
                innerException.printStackTrace()
            }
            outerException.printStackTrace()
        }
        byteBuffer.flip()
        decoder.decode(byteBuffer).toString().encodeToByteArray()
    }
}

fun SocketChannel.writeToTarget(
    byteBuffer: ByteBuffer,
    disconnectedCallback: (SocketChannel.() -> Unit)
) {
    kotlin.runCatching {
        while (!finishConnect()) {
        }
        if (!isConnected) {
            logDebug("socketChannel: $this is now disconnected")
            disconnectedCallback()
            return@runCatching
        }
        while (byteBuffer.hasRemaining()) {
            write(byteBuffer)
        }
    }.onFailure { e ->
        e.printStackTrace()
        kotlin.runCatching {
            close()
        }.onFailure { it.printStackTrace() }
    }
}

fun SocketChannel.responseTo(response: TransportObj, disconnectedCallback: DisconnectedCallback = {}) {
    val serializedString = "${response.toJson()}\n"
    val byteBuffer = ByteBuffer.wrap(serializedString.encodeToByteArray())
    writeToTarget(byteBuffer, disconnectedCallback)
}

fun SocketChannel.responseTo(response: JSONObject, disconnectedCallback: DisconnectedCallback = {}) {
    val serializedString = "${response}\n"
    val byteBuffer = ByteBuffer.wrap(serializedString.encodeToByteArray())
    writeToTarget(byteBuffer, disconnectedCallback)
}

fun SocketChannel.sendRequest(
    buffer: ByteBuffer?,
    request: TransportObj,
    disconnectedCallback: DisconnectedCallback = {}
) {
    val serializedString = request.toJson()
    val byteBuffer = buffer ?: ByteBuffer.wrap(serializedString.encodeToByteArray())
    byteBuffer.clear()
    byteBuffer.put(serializedString.encodeToByteArray())
    byteBuffer.flip()
    writeToTarget(byteBuffer, disconnectedCallback)
}

fun SocketChannel.sendRequest(
    buffer: ByteBuffer?,
    request: JSONObject,
    disconnectedCallback: DisconnectedCallback = {}
) {
    val serializedString = "${request}\n"
    val byteBuffer = buffer ?: ByteBuffer.wrap(serializedString.encodeToByteArray())
    byteBuffer.clear()
    byteBuffer.put(serializedString.encodeToByteArray())
    byteBuffer.flip()
    writeToTarget(byteBuffer, disconnectedCallback)
}

fun SocketChannel.readJSONFromRemote(byteBuffer: ByteBuffer?, encryptor: IEncryptor?): List<JSONObject?> {
    val decodeByteArray = decodeByteArray(byteBuffer)
    if (decodeByteArray == null || decodeByteArray.isEmpty()) {
        return emptyList()
    }
    return encryptor?.decodeToJSONResponse(decodeByteArray) ?: emptyList()
}

private fun IEncryptor.decodeToJSONResponse(byteArray: ByteArray?): List<JSONObject?> {
    byteArray ?: return emptyList()
    val decodeStringResource = decode(byteArray).trim()
    logDebug("on receive message: $decodeStringResource")
    val splitDecodeStringResource = decodeStringResource.split('\n')
    return splitDecodeStringResource.map { splitResource ->
        kotlin.runCatching {
            JSONObject(splitResource)
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }
}
