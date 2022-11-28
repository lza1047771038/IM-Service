package org.im.service.server.controller

import org.im.service.interfaces.IEncryptor
import org.im.service.interfaces.RequestHandler
import org.im.service.interfaces.SocketChannelDispatcher
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
    private val messageQueue: MessageQueue,
    private val dispatcher: SocketChannelDispatcher
) : SocketServerService {

    companion object {
        private var threadsCount: Int = 0
    }

    private val threadName: String by lazy { "${this.javaClass.simpleName}-thread-${threadsCount++}" }

    private val serverSocketChannel: ServerSocketChannel by lazy { ServerSocketChannel.open() }
    private val serverSocket: ServerSocket by lazy { serverSocketChannel.socket() }
    private val selector: Selector by lazy { Selector.open() }

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

            dispatcher.dispatch(selector)
        }
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