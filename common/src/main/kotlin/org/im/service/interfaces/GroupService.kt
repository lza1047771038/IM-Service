package org.im.service.interfaces

interface GroupService {
    fun createGroup(sessionId: String): GroupChannel
    fun destroyGroup(sessionId: String)

    fun getGroupChannel(sessionId: String): GroupChannel?
}