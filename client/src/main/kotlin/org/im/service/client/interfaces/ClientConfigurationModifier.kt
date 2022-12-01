package org.im.service.client.interfaces

import org.im.service.client.impl.MessageDecodeFactory
import org.im.service.client.impl.SessionCallback
import org.im.service.client.interfaces.callback.IMMessageCallback
import org.im.service.metadata.client.Message

/**
 * @author: liuzhongao
 * @date: 2022/12/1 14:22
 */
typealias ModificationHandler = ClientConfigurationModifier.() -> Unit

sealed interface ClientConfigurationModifier {
    companion object {
        @JvmStatic
        fun newModifier(sessionCallback: SessionCallback, decodeFactoryWrapper: MessageDecodeFactory): ClientConfigurationModifier {
            return ClientConfigurationModifierImpl(sessionCallback, decodeFactoryWrapper)
        }
    }

    fun addIMMessageCallback(callback: IMMessageCallback)
    fun removeIMMessageCallback(callback: IMMessageCallback)

    fun addFactory(factory: Message.DecodeFactory)
    fun removeFactory(factory: Message.DecodeFactory)

    fun addFactory(type: Int, factory: Message.DecodeFactory)
    fun removeFactory(type: Int)
}

private class ClientConfigurationModifierImpl(
    private val sessionCallback: SessionCallback,
    private val decodeFactoryWrapper: MessageDecodeFactory
): ClientConfigurationModifier {
    override fun addIMMessageCallback(callback: IMMessageCallback) {
        sessionCallback.addCallback(callback)
    }

    override fun removeIMMessageCallback(callback: IMMessageCallback) {
        sessionCallback.removeCallback(callback)
    }

    override fun addFactory(factory: Message.DecodeFactory) {
        decodeFactoryWrapper.addDecodeFactory(factory)
    }

    override fun addFactory(type: Int, factory: Message.DecodeFactory) {

    }

    override fun removeFactory(factory: Message.DecodeFactory) {

    }

    override fun removeFactory(type: Int) {

    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

}