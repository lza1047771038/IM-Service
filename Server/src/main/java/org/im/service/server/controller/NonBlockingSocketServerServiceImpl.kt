package org.im.service.server.controller

import org.im.service.interfaces.IEncryptor
import org.im.service.interfaces.RequestHandler
import org.im.service.interfaces.SocketServerService
import org.im.service.message.queue.interfaces.MessageQueue
import org.im.service.message.queue.interfaces.execute
import org.im.service.utils.closeSilently
import org.im.service.utils.readRequest
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

class NonBlockingSocketServerServiceImpl(
    override val port: Int,
    private val encryptor: IEncryptor,
    private val requestHandler: RequestHandler,
    private val messageQueue: MessageQueue
) : SocketServerService {

    companion object {
        private var threadsCount: Int = 0
    }

    private val threadName: String by lazy { "${this.javaClass.simpleName}-thread-${threadsCount++}" }

    private val serverSocketChannel: ServerSocketChannel by lazy { ServerSocketChannel.open() }
    private val serverSocket: ServerSocket by lazy { serverSocketChannel.socket() }
    private val selector: Selector by lazy { Selector.open() }
    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(10240)

    private var thread: Thread? = null

    private val threadOperation = Runnable {
        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            try {
                selector.select()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                break
            }

            val selectionKeys = selector.selectedKeys().iterator()
            while (selectionKeys.hasNext()) {
                val selectionKey: SelectionKey? = selectionKeys.next();
                when {
                    selectionKey == null || !selectionKey.isValid -> continue
                    selectionKey.isAcceptable -> selectionKey.acceptConnection()
                    selectionKey.isReadable -> {
                        messageQueue.execute {
                            val byteBuffer = selectionKey.attachment() as? ByteBuffer ?: byteBuffer
                            val socketChannel = selectionKey.channel() as? SocketChannel ?: return@execute
                            val clientRequest = synchronized(byteBuffer) {
                                socketChannel.readRequest(byteBuffer, encryptor) ?: return@execute
                            }
                            requestHandler.handle(socketChannel, clientRequest)
                        }
                    }
                }
                selectionKeys.remove()
            }
        }
    }

    private fun SelectionKey.acceptConnection() {
        val socketServerChannel = channel() as? ServerSocketChannel
        if (socketServerChannel == null) {
            println("socketServerChannel is Empty, return operation")
            return
        }
        val clientChannel = socketServerChannel.accept()
        clientChannel.configureBlocking(false)
        clientChannel.register(selector, SelectionKey.OP_READ)
    }

    override fun startListeningToTargetPort() {
        serverSocket.reuseAddress = true
        serverSocket.bind(InetSocketAddress("127.0.0.1", port))
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
        thread?.interrupt()
        thread = Thread(threadOperation, threadName)
        thread?.start()
    }

    override fun stopListeningToTargetPort() {
        thread?.interrupt()
        thread = null
        serverSocket.closeSilently()
        serverSocketChannel.closeSilently()
        selector.closeSilently()
    }
}