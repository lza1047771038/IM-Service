package org.im.service.client.impl

import org.im.service.client.connection.ClientConnectionManager
import org.im.service.client.impl.handler.ResponseHandlerWrapper
import org.im.service.client.interfaces.*
import org.im.service.metadata.client.IMInitConfig

private val messageClientImpl: MsgClientImpl by lazy { MsgClientImpl() }

val msgClient: MsgClient
    get() = messageClientImpl

internal class MsgClientImpl internal constructor(): MsgClient {

    private val messageParserFactory by lazy { MessageParserFactoryWrapper() }
    private val messageAttachmentParserFactory by lazy { MessageAttachmentFactoryWrapper() }
    private val sessionCallback by lazy { SessionCallback() }
    private val responseHandler by lazy { ResponseHandlerWrapper(sessionCallback) }
    private val connectionManager by lazy { ClientConnectionManager(responseHandler, sessionCallback) }
    private val messageOperator by lazy { P2PMessageOperator(connectionManager) }

    override fun init(imConfig: IMInitConfig) {
        connectionManager.connect(imConfig)
    }

    override fun modification(invocation: ModificationHandler) {
        ClientConfigurationModifier.newModifier(
            responseHandler = responseHandler,
            sessionCallback = sessionCallback,
            decodeFactoryWrapper = messageParserFactory,
            attachmentParserFactory = messageAttachmentParserFactory
        ).invocation()
    }

    override fun authorization(): MsgAuthorization = connectionManager.authorization()

    override fun msgOperator(): MsgOperator = messageOperator

    override fun disconnect() = connectionManager.disconnect()
}