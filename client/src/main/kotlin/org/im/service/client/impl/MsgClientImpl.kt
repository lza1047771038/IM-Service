package org.im.service.client.impl

import org.im.service.client.connection.ClientConnectionManager
import org.im.service.client.impl.handler.ResponseHandlerWrapper
import org.im.service.client.interfaces.*
import org.im.service.client.metadata.IMInitConfig
import org.im.service.client.metadata.SessionType

private val messageClientImpl: MsgClientImpl by lazy { MsgClientImpl() }

val msgClient: MsgClient
    get() = messageClientImpl

internal class MsgClientImpl internal constructor(): MsgClient {

    private val messageParserFactory by lazy { MessageParserFactoryWrapper() }
    private val messageAttachmentParserFactory by lazy { MessageAttachmentFactoryWrapper() }
    private val sessionCallback by lazy { SessionCallback() }
    private val responseHandler by lazy { ResponseHandlerWrapper(sessionCallback) }
    private val connectionManager by lazy { ClientConnectionManager(responseHandler, sessionCallback) }

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

    override fun msgOperator(sessionType: SessionType): MsgOperator {
        return when (sessionType) {
            SessionType.P2P -> P2PMessageOperator(connectionManager)
            SessionType.Group -> GroupMessageOperator(connectionManager)
            else -> throw IllegalArgumentException("unknown sessionType: ${sessionType.name}")
        }
    }

    override fun disconnect() = connectionManager.disconnect()
}