package org.im.service.server.controller.dispatcher

import org.im.service.interfaces.SocketChannelDispatcher
import org.im.service.log.logDebug
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import kotlin.concurrent.thread

/**
 * @author: liuzhongao
 * @date: 2022/11/28 10:40
 */
abstract class AbsSocketChannelDispatcher: SocketChannelDispatcher {

    private val byteBuffer: ByteBuffer = ByteBuffer.allocate(10240)
    private val acceptMessageSelector: Selector = Selector.open()

    private val receiveMessageRunnable = Runnable {
        val currentThread = Thread.currentThread()
        while (currentThread.isAlive && !currentThread.isInterrupted) {
            kotlin.runCatching {
                acceptMessageSelector.select()
            }.onFailure { it.printStackTrace() }
            dispatch(acceptMessageSelector)
        }
    }

    init {
        thread {
            receiveMessageRunnable.run()
        }
    }

    override fun dispatch(selector: Selector) {
        val selectionKeys = selector.selectedKeys().iterator()
        while (selectionKeys.hasNext()) {
            val selectionKey: SelectionKey? = selectionKeys.next();
            when {
                selectionKey == null || !selectionKey.isValid -> return
                selectionKey.isAcceptable -> handleInnerAcceptConnection(selector, selectionKey)
                selectionKey.isReadable -> handleInnerReadable(selector, selectionKey)
            }
            selectionKeys.remove()
        }
    }

    private fun handleInnerAcceptConnection(selector: Selector, selectionKey: SelectionKey) {
        val socketServerChannel = selectionKey.channel() as? ServerSocketChannel
        if (socketServerChannel == null) {
            logDebug("socketServerChannel is Empty, return operation")
            return
        }
        val clientChannel = socketServerChannel.accept()
        if (selector == acceptMessageSelector) {
            selector.wakeup()
        }
        onAcceptConnection(selector, clientChannel)
    }

    private fun handleInnerReadable(selector: Selector, selectionKey: SelectionKey) {
        val byteBuffer = selectionKey.attachment() as? ByteBuffer ?: byteBuffer
        val socketChannel = selectionKey.channel() as? SocketChannel ?: return
        onAcceptReadable(byteBuffer, socketChannel)
    }

    open fun onAcceptConnection(selector: Selector, clientChannel: SocketChannel) {
        clientChannel.configureBlocking(false)
        clientChannel.register(selector, SelectionKey.OP_READ)
    }

    abstract fun onAcceptReadable(byteBuffer: ByteBuffer, socketChannel: SocketChannel)
}