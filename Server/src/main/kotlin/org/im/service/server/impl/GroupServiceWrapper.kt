package org.im.service.server.impl

import org.im.service.interfaces.ClientService
import org.im.service.interfaces.GroupChannel
import org.im.service.interfaces.GroupService
import org.im.service.server.impl.channel.GroupChannelImpl
import java.util.concurrent.ConcurrentHashMap

class GroupServiceWrapper(
    private val clientService: ClientService
): GroupService {

    private val groupServiceMap: MutableMap<String, GroupChannelImpl> = ConcurrentHashMap()

    override fun createGroup(sessionId: String): GroupChannel? {
        var groupChannel: GroupChannelImpl?
        if (!groupServiceMap.containsKey(sessionId)) {
            synchronized(groupServiceMap) {
                if (!groupServiceMap.containsKey(sessionId)) {
                    val exist = groupServiceMap[sessionId]
                    if (exist == null) {
                        val impl = GroupChannelImpl(sessionId, clientService)
                        groupServiceMap[sessionId] = impl
                        groupChannel = impl
                    } else {
                        groupChannel = exist
                    }
                } else {
                    groupChannel = groupServiceMap[sessionId]
                }
            }
        } else {
            groupChannel = groupServiceMap[sessionId]
        }
        return groupChannel
    }

    override fun destroyGroup(sessionId: String) {
        if (groupServiceMap.containsKey(sessionId)) {
            synchronized(groupServiceMap) {
                groupServiceMap[sessionId]?.close()
            }
        }
    }

    override fun getGroupChannel(sessionId: String): GroupChannel? = synchronized(groupServiceMap) { groupServiceMap[sessionId] }
}