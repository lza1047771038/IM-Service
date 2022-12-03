package org.im.service.server.controller

import org.im.service.interfaces.SocketChannelDispatcher
import org.im.service.interfaces.SocketServerService
import org.im.service.utils.closeSilently
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

internal class NonBlockingSocketServerServiceImpl(
    private val address: String,
    override val port: Int,
    private val dispatcher: SocketChannelDispatcher
) : SocketServerService {

    companion object {
        private var threadsCount: Int = 0
    }

    private val threadName: String by lazy { "${this.javaClass.simpleName}-thread-${threadsCount++}" }

    private var serverSocketChannel: ServerSocketChannel? = null
    private var serverSocket: ServerSocket? = null
    private var selector: Selector? = null

    private var thread: Thread? = null

    private val threadOperation = Runnable {
        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            val selector = this.selector
            if (selector == null || !selector.isOpen) {
                break
            }

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
        val localThread = this.thread
        if (localThread != null && localThread.isAlive) {
            return
        }

        stopListeningToTargetPort()

        val serverSocketChannel = ServerSocketChannel.open()
        val serverSocket = serverSocketChannel.socket()
        val selector = Selector.open()

        serverSocket.reuseAddress = true
        serverSocket.bind(InetSocketAddress(address, port))
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)

        this.serverSocketChannel = serverSocketChannel
        this.serverSocket = serverSocket
        this.selector = selector

        thread?.interrupt()
        thread = Thread(threadOperation, threadName)
        thread?.start()
    }

    override fun stopListeningToTargetPort() {
        thread?.interrupt()
        thread = null
        serverSocket?.closeSilently()
        serverSocketChannel?.closeSilently()
        selector?.closeSilently()
        serverSocket = null
        serverSocketChannel = null
        selector = null
    }
}