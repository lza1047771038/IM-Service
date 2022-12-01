package org.im.service.client.interfaces

import org.im.service.metadata.client.IMInitConfig

/**
 * @author: liuzhongao
 * @date: 2022/11/28 14:11
 */
interface MsgClient {

    fun init(imConfig: IMInitConfig)

    fun modification(invocation: ModificationHandler)

    fun authorization(): MsgAuthorization

    fun msgOperator(): MsgOperator

    fun disconnect()
}