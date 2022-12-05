package org.im.service.client.impl

import org.im.service.client.interfaces.Attachment
import org.im.service.client.interfaces.Message
import org.im.service.client.metadata.MsgAccount
import org.im.service.client.metadata.MsgType
import org.im.service.client.metadata.SessionType
import org.im.service.utils.*
import org.json.JSONObject

/**
 * @author: liuzhongao
 * @date: 2022/11/29 19:58
 */
open class MessageImpl(override var uuid: String) : Message {

    protected var fromUserString: String? = null
    protected var toUserString: String? = null

    protected var clientExtensionString: String? = null
    protected var remoteExtensionString: String? = null
    protected var attachmentString: String? = null

    override var textContent: String = ""
    override var fromUserId: String = ""
    override var toUserId: String = ""
    override var msgType: MsgType = MsgType.Unknown
    override var sessionType: SessionType = SessionType.P2P
    override var fromUser: MsgAccount? = null
        set(value) {
            fromUserString = value?.toJson()
            field = value
        }
        get() {
            if (field != null) {
                return field
            }
            return fromUserString
                .takeIf { it != null && it.isNotEmpty() }
                ?.let { JSONObject(it) }
                ?.let { MsgAccount.parseAccount(it) }
                ?.apply { field = this }
        }
    override var toUser: MsgAccount? = null
        set(value) {
            toUserString = value?.toJson()
            field = value
        }
        get() {
            if (field != null) {
                return field
            }
            return toUserString
                .takeIf { it != null && it.isNotEmpty() }
                ?.let { JSONObject(it) }
                ?.let { MsgAccount.parseAccount(it) }
                ?.apply { field = this }
        }
    override var remoteExtensions: MutableMap<String, Any?>? = null
        set(value) {
            remoteExtensionString = kotlin.runCatching { JSONObject(value) }
                .getOrNull()
                ?.toString()
            field = value
        }
        get() {
            if (field != null) {
                return field
            }
            return remoteExtensionString.takeIf { it != null && it.isNotEmpty() }
                ?.let { kotlin.runCatching { JSONObject(remoteExtensionString) } }
                ?.getOrNull()
                ?.toMap()
                ?.apply { field = this }
        }
    override var clientExtensions: MutableMap<String, Any?>? = null
        set(value) {
            clientExtensionString = kotlin.runCatching { JSONObject(value) }
                .getOrNull()
                ?.toString()
            field = value
        }
        get() {
            if (field != null) {
                return field
            }
            return clientExtensionString.takeIf { it != null && it.isNotEmpty() }
                ?.let { kotlin.runCatching { JSONObject(remoteExtensionString) } }
                ?.getOrNull()
                ?.toMap()
                ?.apply { field = this }
        }
    override var attachment: Attachment? = null

    constructor(jsonObject: JSONObject): this(jsonObject.uuid) {
        clientExtensionString = jsonObject.clientExtension
        remoteExtensionString = jsonObject.remoteExtension
        attachmentString = jsonObject.attachment
        fromUserString = jsonObject.fromUser
        toUserString = jsonObject.toUser

        textContent = jsonObject.textContent
        fromUserId = jsonObject.fromUserId
        toUserId = jsonObject.toUserId

        msgType = jsonObject.type
        sessionType = jsonObject.sessionType
    }

}