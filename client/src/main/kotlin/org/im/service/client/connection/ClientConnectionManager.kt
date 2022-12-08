package org.im.service.client.connection

import org.im.service.Const
import org.im.service.client.impl.MsgSessionDelegate
import org.im.service.client.interfaces.*
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.client.utils.IMUserInfo
import org.im.service.client.utils.notifySingleType
import org.im.service.impl.NoEncryptor
import org.im.service.interfaces.IEncryptor
import org.im.service.interfaces.ResponseHandler
import org.im.service.log.logger
import org.im.service.client.metadata.IMInitConfig
import org.im.service.client.metadata.LoginParams
import org.im.service.client.interfaces.Message
import org.im.service.utils.*
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

internal class ClientConnectionManager(
    private val responseHandler: ResponseHandler,
    private val messageCallback: IMMessageCallback,
    private val attachmentParserFactory: Message.AttachmentParserFactory
): SessionOperator {
    private var imInitConfig: IMInitConfig? = null

    private val threadName = "${this.javaClass.simpleName}-thread-0"

    private val encryptor: IEncryptor = NoEncryptor()
    private val receiveBuffer: ByteBuffer by lazy { ByteBuffer.allocate(10240) }

    private var selector: Selector? = null
    private var socketChannel: SocketChannel? = null

    private val threadRunnable = Runnable {
        val socketChannel = this.socketChannel
        val imInitConfig = this.imInitConfig
        if (socketChannel == null || imInitConfig == null) {
            return@Runnable
        } else {
            val address = InetSocketAddress(imInitConfig.serverAddress, imInitConfig.port)
            socketChannel.connect(address)
        }

        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            val selector = this.selector ?: break

            kotlin.runCatching {
                selector.select()
            }.onFailure { exception ->
                exception.printStackTrace()
            }

            if (!socketChannel.isOpen) {
                messageCallback.notifySingleType(Const.Code.SESSION_DISCONNECTED)
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
                        messageCallback.notifySingleType(Const.Code.CONNECTION_ESTABLISHED)
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
        override val channel: SocketChannel?
            get() = socketChannel
        override val writeBuffer: ByteBuffer
            get() = receiveBuffer
    }

    private val msgAuthorization = object: MsgAuthorization {
        override fun login(params: LoginParams): Boolean {
            val socketChannel = this@ClientConnectionManager.socketChannel
            if (socketChannel == null) {
                logger.log("Authorization", "please call ClientConnectionManager#init first, before login")
                return false
            }

            if (!socketChannel.isOpen) {
                logger.log("Authorization", "connect not established, skip current operation.")
                return false
            }
            while (!socketChannel.finishConnect()) {}
            IMUserInfo.selfUserId = params.uid
            IMUserInfo.selfSessionId = params.sessionId
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
        val socketChannel = SocketChannel.open()
        val selector = Selector.open()
        socketChannel.configureBlocking(false)
        socketChannel.register(selector, SelectionKey.OP_CONNECT)

        this.socketChannel = socketChannel
        this.selector = selector

        lastThread?.interrupt()
        lastThread = Thread(threadRunnable, threadName)
        lastThread?.start()
    }

    fun authorization(): MsgAuthorization = msgAuthorization

    override fun sendMessage(message: Message) = sessionOperatorDelegate.sendMessage(message)
    override fun sendMessage(message: Message, messageProgressCallback: MessageProgressCallback?) = sessionOperatorDelegate.sendMessage(message, messageProgressCallback)

    override fun deleteMessage(message: Message) = sessionOperatorDelegate.deleteMessage(message)

    fun disconnect() {
        selector?.closeSilently()
        socketChannel?.closeSilently()
        kotlin.runCatching { receiveBuffer.reset() }
        selector = null
        socketChannel = null
    }
}