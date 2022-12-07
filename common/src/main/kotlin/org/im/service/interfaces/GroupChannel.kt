package org.im.service.interfaces

interface GroupChannel: Channel {
    fun containsClient(sessionId: String): Boolean

    fun joinGroup(sessionId: String)
    fun quitGroup(sessionId: String)
}