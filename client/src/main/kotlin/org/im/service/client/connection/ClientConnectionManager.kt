package org.im.service.client.connection

import org.im.service.Const
import org.im.service.client.impl.MsgSessionDelegate
import org.im.service.client.interfaces.*
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.metadata.NotifyWrapper
import org.im.service.client.utils.IMUserInfo
import org.im.service.impl.NoEncryptor
import org.im.service.interfaces.IEncryptor
import org.im.service.interfaces.ResponseHandler
import org.im.service.log.logger
import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.LoginParams
import org.im.service.metadata.client.Message
import org.im.service.utils.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

internal class ClientConnectionManager(
    private val responseHandler: ResponseHandler,
    private val messageCallback: IMMessageCallback
): SessionOperator {
    private var imInitConfig: IMInitConfig? = null

    private val threadName = "${this.javaClass.simpleName}-thread-0"

    private val socketChannel: SocketChannel by lazy { SocketChannel.open() }
    private val receiveBuffer: ByteBuffer by lazy { ByteBuffer.allocate(10240) }
    private val selector: Selector by lazy { Selector.open() }
    private val encryptor: IEncryptor = NoEncryptor()

    private val threadRunnable = Runnable {
        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            kotlin.runCatching {
                selector.select()
            }.onFailure { exception ->
                exception.printStackTrace()
            }

            if (!socketChannel.isOpen) {
                val wrapper = NotifyWrapper()
                wrapper.code = Const.Code.SESSION_DISCONNECTED
                messageCallback.onNotify(wrapper)
                break
            }

            val iterator = selector.selectedKeys().iterator()
            while (iterator.hasNext()) {
                val selectionKey = iterator.next()
                iterator.remove()
                when {
                    selectionKey == null || !selectionKey.isValid -> continue
                    selectionKey.isConnectable -> {
                        socketChannel.register(selector, SelectionKey.OP_READ)
                        val wrapper = NotifyWrapper()
                        wrapper.code = Const.Code.CONNECTION_ESTABLISHED
                        messageCallback.onNotify(wrapper)
                    }
                    selectionKey.isReadable -> {
                        val responseJSONObjects = socketChannel.readJSONFromRemote(receiveBuffer, encryptor)
                        responseJSONObjects.forEach { jsonObject ->
                            if (jsonObject != null) {
                                val method = jsonObject.method
                                responseHandler.handle(method, jsonObject)
                            }
                        }
                    }
                }
            }
        }
    }
    private val sessionOperatorDelegate = object: MsgSessionDelegate(messageCallback) {
        override val channel: SocketChannel
            get() = socketChannel
        override val writeBuffer: ByteBuffer
            get() = receiveBuffer
    }

    private val msgAuthorization = object: MsgAuthorization {
        override fun login(params: LoginParams): Boolean {
            if (!socketChannel.isOpen) {
                logger.log("Authorization", "connect not established, skip current operation.")
                return false
            }
            while (!socketChannel.finishConnect()) {}
            IMUserInfo.selfUserId = params.uid
            IMUserInfo.selfSessionId = params.userToken
            val message = createLoginMessage()
            sendMessage(message)
            return true
        }

        override fun logout() {

        }
    }

    private var lastThread: Thread? = null

    fun connect(imInitConfig: IMInitConfig) {
        this.imInitConfig = imInitConfig
        val address = InetSocketAddress(imInitConfig.serverAddress, imInitConfig.port)
        socketChannel.configureBlocking(false)
        socketChannel.register(selector, SelectionKey.OP_CONNECT)
        socketChannel.connect(address)

        lastThread?.interrupt()
        lastThread = Thread(threadRunnable, threadName)
        lastThread?.start()
    }

    fun authorization(): MsgAuthorization = msgAuthorization

    override fun sendMessage(message: Message) = sessionOperatorDelegate.sendMessage(message)

    fun disconnect() {
        selector.closeSilently()
        socketChannel.closeSilently()
        receiveBuffer.reset()
    }
}