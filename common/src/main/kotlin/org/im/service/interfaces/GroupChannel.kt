package org.im.service.interfaces

interface GroupChannel: Channel {

    fun joinGroup(sessionId: String)
    fun quitGroup(sessionId: String)
}