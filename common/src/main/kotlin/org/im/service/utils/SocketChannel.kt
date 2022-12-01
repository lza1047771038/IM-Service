package org.im.service.utils

import org.im.service.interfaces.IEncryptor
import org.im.service.log.logDebug
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

private val decoder = Charsets.UTF_8

typealias DisconnectedCallback = SocketChannel.() -> Unit
typealias OnError = (Throwable) -> Unit

fun SocketChannel.decodeByteArray(byteBuffer: ByteBuffer?, disconnectedCallback: DisconnectedCallback): ByteArray? {
    if (byteBuffer == null || !isConnected || !isOpen) {
        return null
    }
    return synchronized(byteBuffer) {
        byteBuffer.clear()

        kotlin.runCatching {
            var flag: Int
            while (true) {
                flag = read(byteBuffer)
                if (flag == 0 || flag == -1) {
                    break
                }
            }
            if (flag == -1) {
                closeSilently()
                disconnectedCallback.invoke(this)
            }
        }.onFailure { outerException ->
            closeSilently()
            disconnectedCallback.invoke(this)
            outerException.printStackTrace()
        }
        byteBuffer.flip()
        decoder.decode(byteBuffer).toString().encodeToByteArray()
    }
}

fun SocketChannel.writeToTarget(
    byteBuffer: ByteBuffer,
    disconnectedCallback: DisconnectedCallback,
    onError: OnError
) {
    kotlin.runCatching {
        if (!isOpen) {
            disconnectedCallback()
            return@runCatching
        }
        // wait until channel connected
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
        closeSilently()
        onError(e)
    }
}

fun SocketChannel.responseTo(response: JSONObject, disconnectedCallback: DisconnectedCallback = {}, onError: OnError = {}) {
    responseTo(response.toString(), disconnectedCallback, onError)
}

fun SocketChannel.responseTo(response: String, disconnectedCallback: DisconnectedCallback = {}, onError: OnError = {}) {
    val serializedString = "${response}\n"
    val byteBuffer = ByteBuffer.wrap(serializedString.encodeToByteArray())
    writeToTarget(byteBuffer, disconnectedCallback, onError)
}

fun SocketChannel.sendRequest(
    buffer: ByteBuffer?,
    request: JSONObject,
    disconnectedCallback: DisconnectedCallback = {},
    onError: OnError = {}
) {
    val serializedString = "${request}\n"
    val byteBuffer = buffer ?: ByteBuffer.wrap(serializedString.encodeToByteArray())
    byteBuffer.clear()
    byteBuffer.put(serializedString.encodeToByteArray())
    byteBuffer.flip()
    writeToTarget(byteBuffer, disconnectedCallback, onError)
}

fun SocketChannel.readJSONFromRemote(byteBuffer: ByteBuffer?, encryptor: IEncryptor?, disconnectedCallback: DisconnectedCallback = {}): List<JSONObject?> {
    val decodeByteArray = decodeByteArray(byteBuffer, disconnectedCallback)
    if (decodeByteArray == null || decodeByteArray.isEmpty()) {
        return emptyList()
    }
    return encryptor?.decodeToJSONResponse(decodeByteArray) ?: emptyList()
}

private fun IEncryptor.decodeToJSONResponse(byteArray: ByteArray?): List<JSONObject?> {
    byteArray ?: return emptyList()
    val decodeStringResource = decode(byteArray).trim()
    val splitDecodeStringResource = decodeStringResource.split('\n')
    return splitDecodeStringResource.map { splitResource ->
        kotlin.runCatching {
            JSONObject(splitResource)
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }
}
