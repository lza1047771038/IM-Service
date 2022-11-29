package org.im.service.client.impl

import org.im.service.client.connection.ClientConnectionManager
import org.im.service.client.impl.handler.ResponseHandlerWrapper
import org.im.service.client.interfaces.*
import org.im.service.metadata.client.IMInitConfig
import org.im.service.metadata.client.LoginParams
import org.im.service.metadata.client.Message

private val messageClientImpl: MsgClientImpl by lazy { MsgClientImpl() }

val msgClient: MsgClient
    get() = messageClientImpl

internal class MsgClientImpl internal constructor(): MsgClient {

    private val messageDecodeFactory by lazy { MessageDecodeFactory() }
    private val globalCallbackWrapper by lazy { GlobalCallbackWrapper() }
    private val responseHandler by lazy { ResponseHandlerWrapper(messageDecodeFactory, globalCallbackWrapper) }
    private val connectionManager by lazy { ClientConnectionManager(responseHandler, globalCallbackWrapper) }
    private val messageOperator by lazy { MessageOperatorImpl(connectionManager) }

    override fun init(imConfig: IMInitConfig) {
        connectionManager.connect(imConfig)
    }

    override fun addGlobalCallback(callback: GlobalCallback) {
        globalCallbackWrapper.addCallback(callback)
    }

    override fun setOnReceiveResponseListener(onReceiveResponseListener: OnReceiveResponseListener) {
        connectionManager.setOnReceiveResponseListener(onReceiveResponseListener)
    }

    override fun addFactory(factory: Message.Factory) {
        messageDecodeFactory.addDecodeFactory(factory)
    }

    override fun authorization(): MsgAuthorization = connectionManager.authorization()

    override fun msgOperator(): MsgOperator = messageOperator
}