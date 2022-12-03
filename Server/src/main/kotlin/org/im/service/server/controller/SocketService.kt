package org.im.service.server.controller

import org.im.service.interfaces.*
import org.im.service.message.queue.MessageQueueImpl
import org.im.service.message.queue.interfaces.MessageQueue
import org.im.service.server.controller.config.SocketConfig
import org.im.service.server.controller.dispatcher.SocketChannelDispatcherImpl
import org.im.service.server.factory.SocketServerServiceFactory
import org.im.service.server.impl.ClientServiceWrapper
import org.im.service.server.impl.handler.RequestHandlerWrapper
import org.im.service.utils.DisconnectedCallback
import java.util.concurrent.ConcurrentHashMap

class SocketService {
    private val serverWrapper by lazy { SocketServersWrapper() }
    private val clientService by lazy { ClientServiceWrapper() }
    private val requestWrapper by lazy { RequestHandlerWrapper(clientService) }

    private val disconnectedCallback: DisconnectedCallback = { clientService.removeClient(this) }

    private var messageQueue: MessageQueue? = null
    private var channelDispatcher: SocketChannelDispatcher? = null

    @Synchronized
    fun init(config: SocketConfig) {
        val messageQueue = config.messageQueue
        val channelDispatcher = SocketChannelDispatcherImpl(messageQueue, requestWrapper, disconnectedCallback)
        channelDispatcher.encryptor = config.encryptor
        serverWrapper.init(config, channelDispatcher)
        serverWrapper.start()

        this.messageQueue = config.messageQueue
        this.channelDispatcher = channelDispatcher
    }
}

class SocketServersWrapper {

    private val socketServers: MutableMap<Int, SocketServerService> = ConcurrentHashMap()

    fun init(config: SocketConfig, socketServiceDispatcher: SocketChannelDispatcher) {
        val ports: IntArray = config.ports
        val socketServers = HashMap<Int, SocketServerService>()

        for (port in ports) {
            val socketServerService = SocketServerServiceFactory.create(config.address, port, socketServiceDispatcher)
            socketServers[socketServerService.port] = socketServerService
        }

        this.socketServers.putAll(socketServers)
    }

    fun start() {
        socketServers.values.forEach {
            it.startListeningToTargetPort()
        }
    }
}