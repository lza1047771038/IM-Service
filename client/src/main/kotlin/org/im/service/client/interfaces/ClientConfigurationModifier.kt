package org.im.service.client.interfaces

import org.im.service.client.impl.MessageAttachmentFactoryWrapper
import org.im.service.client.impl.SessionCallback
import org.im.service.client.impl.handler.ResponseHandlerWrapper
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.interfaces.ResponseHandler

/**
 * @author: liuzhongao
 * @date: 2022/12/1 14:22
 */
typealias ModificationHandler = ClientConfigurationModifier.() -> Unit

sealed interface ClientConfigurationModifier {
    companion object {
        internal fun newModifier(sessionCallback: SessionCallback, responseHandler: ResponseHandlerWrapper, attachmentParserFactory: MessageAttachmentFactoryWrapper): ClientConfigurationModifier {
            return ClientConfigurationModifierImpl(sessionCallback, responseHandler, attachmentParserFactory)
        }
    }

    fun addIMMessageCallback(callback: IMMessageCallback)
    fun removeIMMessageCallback(callback: IMMessageCallback)

    fun addFactory(factory: Message.AttachmentParserFactory)
    fun removeFactory(factory: Message.AttachmentParserFactory)

    fun addFactory(type: Int, factory: Message.AttachmentParserFactory)
    fun removeAttachmentFactory(type: Int)

    fun addResponseHandler(method: String, handler: ResponseHandler)
    fun removeResponseHandler(method: String)
}

private class ClientConfigurationModifierImpl(
    private val sessionCallback: SessionCallback,
    private val responseHandler: ResponseHandlerWrapper,
    private val attachmentParserFactory: MessageAttachmentFactoryWrapper
): ClientConfigurationModifier {
    override fun addIMMessageCallback(callback: IMMessageCallback) {
        sessionCallback.addCallback(callback)
    }

    override fun removeIMMessageCallback(callback: IMMessageCallback) {
        sessionCallback.removeCallback(callback)
    }

    // message attachment
    override fun addFactory(factory: Message.AttachmentParserFactory) {
        attachmentParserFactory.addFactory(factory)
    }

    override fun addFactory(type: Int, factory: Message.AttachmentParserFactory) {}

    override fun removeFactory(factory: Message.AttachmentParserFactory) {}

    override fun removeAttachmentFactory(type: Int) {}

    override fun addResponseHandler(method: String, handler: ResponseHandler) {
        responseHandler.addHandler(method, handler)
    }

    override fun removeResponseHandler(method: String) {
        responseHandler.removeHandler(method)
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

}