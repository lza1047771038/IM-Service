package org.im.service.client.connection

import org.im.service.Const
import org.im.service.client.impl.MsgSessionDelegate
import org.im.service.client.interfaces.GlobalCallback
import org.im.service.client.interfaces.MsgAuthorization
import org.im.service.client.interfaces.OnReceiveResponseListener
import org.im.service.client.interfaces.SessionOperator
import org.im.service.client.utils.IMUserInfo
import org.im.service.impl.NoEncryptor
import org.im.service.interfaces.IEncryptor
import org.im.service.interfaces.ResponseHandler
import org.im.service.metadata.*
import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.LoginParams
import org.im.service.metadata.client.MsgAccount
import org.im.service.utils.method
import org.im.service.utils.readJSONFromRemote
import org.im.service.utils.sendRequest
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

internal class ClientConnectionManager(
    private val responseHandler: ResponseHandler,
    private val globalCallback: GlobalCallback
): SessionOperator {
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
                break
            }

            val iterator = selector.selectedKeys().iterator()
            while (iterator.hasNext()) {
                val selectionKey = iterator.next()
                iterator.remove()
                when {
                    selectionKey == null || !selectionKey.isValid -> continue
                    selectionKey.isConnectable -> {
                        globalCallback.onConnectionSuccess()
                        socketChannel.register(selector, SelectionKey.OP_READ)
                    }
                    selectionKey.isReadable -> {
                        val responseJSONObjects = socketChannel.readJSONFromRemote(receiveBuffer, encryptor)
                        responseJSONObjects.forEach { jsonObject ->
                            if (jsonObject == null) {
                                return@forEach
                            }
                            val method = jsonObject.method
                            responseHandler.handle(method, jsonObject)
                        }
                    }
                }
            }
        }
    }
    private val sessionOperatorDelegate = object: MsgSessionDelegate() {
        override fun realSendMessage(transportObj: TransportObj) {
            synchronized(receiveBuffer) {
                socketChannel.sendRequest(receiveBuffer, transportObj)
            }
        }
    }

    private val msgAuthorization = object: MsgAuthorization {
        override fun login(params: LoginParams) {
            IMUserInfo.selfUserId = params.uid
            val request = TransportObj(method = Const.Method.USER_AUTHORIZATION)
            request.fromUser = MsgAccount("")
            request.toUser = MsgAccount("")
            request.fromUserId = params.uid
            request.toUserId = params.uid
            sendMessage(request)
        }

        override fun logout() {

        }
    }

    private var lastThread: Thread? = null
    private var receiveResponseListener: OnReceiveResponseListener? = null

    fun connect(imInitConfig: IMInitConfig) {
        val address = InetSocketAddress(imInitConfig.serverAddress, imInitConfig.port)
        socketChannel.configureBlocking(false)
        socketChannel.register(selector, SelectionKey.OP_CONNECT)
        socketChannel.connect(address)

        lastThread?.interrupt()
        lastThread = Thread(threadRunnable, threadName)
        lastThread?.start()
    }

    fun setOnReceiveResponseListener(receiveResponseListener: OnReceiveResponseListener) {
        this.receiveResponseListener = receiveResponseListener
    }

    fun authorization(): MsgAuthorization = msgAuthorization

    override fun sendMessage(transportObj: TransportObj) = sessionOperatorDelegate.sendMessage(transportObj)
}