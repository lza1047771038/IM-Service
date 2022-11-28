package org.im.service.server.controller

import org.im.service.interfaces.*
import org.im.service.message.queue.MessageQueueImpl
import org.im.service.message.queue.interfaces.MessageQueue
import org.im.service.server.controller.config.SocketConfig
import org.im.service.server.controller.dispatcher.SocketChannelDispatcherImpl
import org.im.service.server.factory.SocketServerServiceFactory
import org.im.service.server.impl.ClientServiceWrapper
import org.im.service.server.impl.handler.RequestHandlerWrapper

class SocketService {
    private val serverWrapper: SocketServersWrapper by lazy { SocketServersWrapper() }
    private val messageQueue: MessageQueue by lazy { MessageQueueImpl(3) }
    private val clientService: ClientService by lazy { ClientServiceWrapper() }
    private val requestWrapper: RequestHandler by lazy { RequestHandlerWrapper(clientService) }
    private val channelDispatcher: SocketChannelDispatcherImpl by lazy { SocketChannelDispatcherImpl(messageQueue, requestWrapper) }

    @Synchronized
    fun init(config: SocketConfig) {
        channelDispatcher.encryptor = config.encryptor
        serverWrapper.init(config, requestWrapper, messageQueue, channelDispatcher)
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

    fun init(config: SocketConfig, requestHandler: RequestHandler, messageQueue: MessageQueue, socketServiceDispatcher: SocketChannelDispatcher) {
        val ports: IntArray = config.ports
        val encryptor: IEncryptor = config.encryptor
        val socketServers = HashMap<Int, SocketServerService>()

        for (port in ports) {
            val socketServerService = SocketServerServiceFactory.create(port, encryptor, requestHandler, messageQueue, socketServiceDispatcher)
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