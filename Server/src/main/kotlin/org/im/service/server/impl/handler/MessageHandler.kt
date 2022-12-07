package org.im.service.server.impl.handler

import org.im.service.Const
import org.im.service.client.metadata.MsgState
import org.im.service.client.metadata.SessionType
import org.im.service.common.ServiceFacade
import org.im.service.common.spring.ICrossDatabaseStorage
import org.im.service.common.spring.ICrossUserMessageAuthorization
import org.im.service.interfaces.ClientService
import org.im.service.interfaces.GroupService
import org.im.service.interfaces.Logger
import org.im.service.interfaces.RequestHandler
import org.im.service.log.logger
import org.im.service.utils.*
import org.json.JSONObject
import org.json.JSONTokener
import java.nio.channels.SocketChannel

class MessageHandler(
    private val clientService: ClientService,
    private val groupService: GroupService
): RequestHandler {

    override fun handle(method: String, jsonObject: JSONObject, socketChannel: SocketChannel) {
        val toSessionId = jsonObject.toSessionId
        if (toSessionId.isEmpty()) {
            logger.log("MessageConsumer", "send message with empty user id token")
            return
        }

        logger.log("MessageConsumer", "received message from: ${jsonObject.fromUserId}, content: $jsonObject")

        // 是否封禁
        val checkBothIsBlack = kotlin.runCatching {
            ServiceFacade.get(ICrossUserMessageAuthorization::class.java)?.checkUserIsBlocked(jsonObject)
        }.getOrElse { true } ?: true
        if (checkBothIsBlack) {
            logger.log("MessageConsumer", "one of the user is been blocked, will skip current message operation.")
        }

        // 是否互相有拉黑(禁言)行为
        val hasMuteBehavior = kotlin.runCatching {
            ServiceFacade.get(ICrossUserMessageAuthorization::class.java)?.checkUserMuteState(jsonObject)
        }.getOrElse { true } ?: true
        if (hasMuteBehavior) {
            logger.log("MessageConsumer", "one of the user is been muted by another one, will skip current message operation.")
        }

        // 业务检查
        val businessCheck = kotlin.runCatching {
            ServiceFacade.get(ICrossUserMessageAuthorization::class.java)?.canSendToTarget(jsonObject)
        }.getOrElse { false } ?: false
        if (!businessCheck) {
            logger.log("MessageConsumer", "message cannot be sent to target: ${toSessionId}")
        }

        val canSendMessageToTarget = !(checkBothIsBlack || hasMuteBehavior || !businessCheck)
        if (canSendMessageToTarget) {
            handleSendMessageToTarget(toSessionId, jsonObject)
        }

        kotlin.runCatching {
            ServiceFacade.get(ICrossDatabaseStorage::class.java)?.onReceiveMessage(jsonObject)
        }.onFailure { it.printStackTrace() }
        notifyMessageState(jsonObject, canSendMessageToTarget)
    }

    private fun handleSendMessageToTarget(toSessionId: String, jsonObject: JSONObject) {
        when (jsonObject.sessionType) {
            SessionType.P2P -> handleP2PMessages(toSessionId, jsonObject)
            SessionType.Group -> handleGroupMessages(toSessionId, jsonObject)
            else -> {}
        }
    }

    private fun handleP2PMessages(toSessionId: String, jsonObject: JSONObject) {
        val isUserOnline = clientService.contains(toSessionId)
        if (!isUserOnline) {
            logger.log("MessageConsumer", "user: $toSessionId is not online", Logger.Type.Error)
            return
        } else {
            clientService.getChannel(toSessionId)?.writeResponse(jsonObject)
        }
    }

    private fun handleGroupMessages(toSessionId: String, jsonObject: JSONObject) {
        groupService.getGroupChannel(toSessionId)?.writeResponse(jsonObject)
    }

    private fun notifyMessageState(jsonObject: JSONObject, success: Boolean) {
        val serializedString = jsonObject.toString()
        val jsonTokener = JSONTokener(serializedString)
        val newResponse = JSONObject(jsonTokener)
        val fromUserSessionId = newResponse.fromSessionId
        if (fromUserSessionId.isNotEmpty()) {
            newResponse.method = Const.Method.MESSAGE_STATE_UPDATE
            newResponse.msgState = if (success) { MsgState.Success } else { MsgState.Failed }
            clientService.getChannel(fromUserSessionId)?.writeResponse(newResponse)
        }
    }
}