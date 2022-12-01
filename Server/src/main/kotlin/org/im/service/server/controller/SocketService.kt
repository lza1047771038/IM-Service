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

class SocketService {
    private val serverWrapper by lazy { SocketServersWrapper() }
    private val messageQueue by lazy { MessageQueueImpl(3) }
    private val clientService by lazy { ClientServiceWrapper() }
    private val requestWrapper by lazy { RequestHandlerWrapper(clientService) }
    private val channelDispatcher by lazy { SocketChannelDispatcherImpl(messageQueue, requestWrapper, disconnectedCallback) }

    private val disconnectedCallback: DisconnectedCallback = { clientService.removeClient(this) }

    @Synchronized
    fun init(config: SocketConfig) {
        channelDispatcher.encryptor = config.encryptor
        serverWrapper.init(config, channelDispatcher)
        serverWrapper.start()
    }
}

class SocketServersWrapper {
    private var socketServersMutable: Boolean = true

    private var socketServers: Map<Int, SocketServerService> = mapOf()
        private set(value) {
            if (!socketServersMutable) {
                return
            }
            field = value
        }

    fun init(config: SocketConfig, socketServiceDispatcher: SocketChannelDispatcher) {
        val ports: IntArray = config.ports
        val socketServers = HashMap<Int, SocketServerService>()

        for (port in ports) {
            val socketServerService = SocketServerServiceFactory.create(config.address, port, socketServiceDispatcher)
            socketServers[socketServerService.port] = socketServerService
        }

        this.socketServers = socketServers
        this.socketServersMutable = false
    }

    fun start() {
        socketServers.values.forEach {
            it.startListeningToTargetPort()
        }
    }
}