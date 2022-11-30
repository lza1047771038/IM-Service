package org.im.service.client.impl

import org.im.service.client.connection.ClientConnectionManager
import org.im.service.client.impl.handler.ResponseHandlerWrapper
import org.im.service.client.interfaces.*
import org.im.service.client.interfaces.callback.GlobalCallback
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.Message

private val messageClientImpl: MsgClientImpl by lazy { MsgClientImpl() }

val msgClient: MsgClient
    get() = messageClientImpl

internal class MsgClientImpl internal constructor(): MsgClient {

    private val messageDecodeFactory by lazy { MessageDecodeFactory() }
    private val sessionCallback by lazy { SessionCallback() }
    private val responseHandler by lazy { ResponseHandlerWrapper(messageDecodeFactory, sessionCallback) }
    private val connectionManager by lazy { ClientConnectionManager(responseHandler, sessionCallback) }
    private val messageOperator by lazy { MessageOperatorImpl(connectionManager) }

    override fun init(imConfig: IMInitConfig) {
        connectionManager.connect(imConfig)
    }

    override fun addMessageCallback(callback: IMMessageCallback) {
        sessionCallback.addCallback(callback)
    }

    override fun removeMessageCallback(callback: IMMessageCallback) {
        sessionCallback.removeCallback(callback)
    }

    override fun setOnReceiveResponseListener(onReceiveResponseListener: OnReceiveResponseListener) {
        connectionManager.setOnReceiveResponseListener(onReceiveResponseListener)
    }

    override fun addFactory(factory: Message.Factory) {
        messageDecodeFactory.addDecodeFactory(factory)
    }

    override fun authorization(): MsgAuthorization = connectionManager.authorization()

    override fun msgOperator(): MsgOperator = messageOperator

    override fun disconnect() = connectionManager.disconnect()
}