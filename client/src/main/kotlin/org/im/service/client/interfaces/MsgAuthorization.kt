package org.im.service.client.interfaces

import org.im.service.client.metadata.LoginParams

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:11
 */
interface MsgAuthorization {
    fun login(params: LoginParams): Boolean
    fun logout()
}