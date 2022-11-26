package org.im.service.utils

import org.im.service.moshi.moshi
import org.im.service.interfaces.IEncryptor
import org.im.service.metadata.ClientRequest
import org.im.service.metadata.ServerResponse
import org.im.service.metadata.toJson
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

private val decoder = Charsets.UTF_8

fun SocketChannel.decodeByteArray(byteBuffer: ByteBuffer?): ByteArray? {
    if (byteBuffer == null) {
        return null
    }
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
    return decoder.decode(byteBuffer).toString().encodeToByteArray()
}

fun SocketChannel.writeToTarget(byteBuffer: ByteBuffer) {
    kotlin.runCatching {
        while (!finishConnect()) {}
        if (!isConnected) {
            println("socketChannel: $this is now disconnected")
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

// for server
fun SocketChannel.readRequest(byteBuffer: ByteBuffer?, encryptor: IEncryptor?): ClientRequest? {
    val decodeByteArray = decodeByteArray(byteBuffer)
    if (decodeByteArray == null || decodeByteArray.isEmpty()) {
        return null
    }
    return encryptor?.decodeRequest(decodeByteArray)
}

fun SocketChannel.responseTo(response: ServerResponse) {
    val serializedString = response.toJson()
    val byteBuffer = ByteBuffer.wrap(serializedString.encodeToByteArray())
    writeToTarget(byteBuffer)
}

private fun IEncryptor.decodeRequest(byteArray: ByteArray?): ClientRequest? {
    byteArray ?: return null
    val decodeStringResource = decode(byteArray).trim()
    println("on receive message: $decodeStringResource")
    val adapter = moshi.adapter<ClientRequest>(ClientRequest::class.java)
    return kotlin.runCatching {
        adapter.fromJson(decodeStringResource)
    }.onFailure { it.printStackTrace() }.getOrNull()
}

// for client
fun SocketChannel.readResponse(byteBuffer: ByteBuffer?, encryptor: IEncryptor?): ServerResponse? {
    val decodeByteArray = decodeByteArray(byteBuffer)
    if (decodeByteArray == null || decodeByteArray.isEmpty()) {
        return null
    }
    return encryptor?.decodeResponse(decodeByteArray)
}

fun SocketChannel.sendRequest(buffer: ByteBuffer?, request: ClientRequest) {
    val serializedString = request.toJson()
    val byteBuffer = buffer ?: ByteBuffer.wrap(serializedString.encodeToByteArray())
    byteBuffer.clear()
    byteBuffer.put(serializedString.encodeToByteArray())
    byteBuffer.flip()
    writeToTarget(byteBuffer)
}

private fun IEncryptor.decodeResponse(byteArray: ByteArray?): ServerResponse? {
    byteArray ?: return null
    val decodeStringResource = decode(byteArray).trim()
    val adapter = moshi.adapter(ServerResponse::class.java)
    return kotlin.runCatching {
        adapter.fromJson(decodeStringResource)
    }.onFailure { it.printStackTrace() }.getOrNull()
}
