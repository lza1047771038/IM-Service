package org.im.service.server.impl

import org.im.service.client.metadata.MsgState
import org.im.service.client.metadata.SessionType
import org.im.service.common.message.ICrossGroupOperator
import org.im.service.common.message.ICrossMessageOperator
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.GroupService
import org.im.service.log.logDebug
import org.im.service.log.logger
import org.im.service.utils.obtainTextMessageWithFullParameter
import org.im.service.utils.toJSONObj

/**
 * @author liuzhongao
 * @version 2022/12/7 16:06
 */

class CrossMessageOperator(
    private val clientService: ClientService,
    private val groupService: GroupService
): ICrossMessageOperator {

    override fun sendTextMessage(fromSessionId: String, targetSessionId: String, sessionType: SessionType, content: String, remoteExtension: MutableMap<String, Any>?): Boolean {
        val textMessage = obtainTextMessageWithFullParameter(
            fromSessionId = fromSessionId,
            toSessionId = targetSessionId,
            sessionType = sessionType,
            textContent = content,
            remoteExtension = remoteExtension as MutableMap<String, Any?>
        )

        textMessage.msgState = MsgState.Success

        when (sessionType) {
            SessionType.P2P -> clientService.getChannel(targetSessionId)
            SessionType.Group -> groupService.getGroupChannel(targetSessionId)
            else -> null
        }?.writeResponse(textMessage.toJSONObj())
        return true
    }

    @Deprecated("", ReplaceWith("false"))
    override fun sendImageMessage(fromSessionId: String, sessionId: String, sessionType: SessionType, remoteExtension: MutableMap<String, Any>?): Boolean {
        return false
    }
}

class CrossGroupOperator(
    private val groupService: GroupService
): ICrossGroupOperator {
    override fun createGroup(sessionId: String): Boolean {
        return groupService.createGroup(sessionId) != null
    }

    override fun destroyGroup(sessionId: String): Boolean {
        groupService.destroyGroup(sessionId)
        return true
    }

    override fun joinGroup(groupSessionId: String, userSessionId: String): Boolean {
        val groupChannel = groupService.getGroupChannel(groupSessionId)
        if (groupChannel == null) {
            logger.logDebug("Group: $groupSessionId does not exist, please call ICrossGroupOperator#create(String) first.")
            return false
        }
        if (groupChannel.containsClient(userSessionId)) {
            return true
        }
        return kotlin.runCatching {
            groupChannel.joinGroup(userSessionId)
            true
        }.onFailure { it.printStackTrace() }
            .getOrElse { false }
    }

    override fun quitGroup(groupSessionId: String, userSessionId: String): Boolean {
        val groupChannel = groupService.getGroupChannel(groupSessionId)
        if (groupChannel == null) {
            logger.logDebug("Group: $groupSessionId does not exist, please call ICrossGroupOperator#create(String) first.")
            return false
        }

        return kotlin.runCatching {
            groupChannel.quitGroup(userSessionId)
            true
        }.onFailure { it.printStackTrace() }
            .getOrElse { false }
    }

}