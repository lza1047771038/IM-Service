package org.im.service.client.interfaces

import org.im.service.metadata.client.LoginParams

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:11
 */
interface MsgAuthorization {
    fun login(params: LoginParams)
    fun logout()
}