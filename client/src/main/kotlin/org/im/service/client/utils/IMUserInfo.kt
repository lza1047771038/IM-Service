package org.im.service.client.utils

import org.im.service.metadata.client.MsgAccount

/**
 * @author: liuzhongao
 * @date: 2022/11/29 14:03
 */
internal object IMUserInfo {
    var selfUserId: String = ""
    var selfSessionId: String = ""
    var selfAccount: MsgAccount? = null
        get() {
            if (field == null && selfSessionId.isNotEmpty()) {
                field = MsgAccount(selfSessionId)
            }
            return field
        }

}